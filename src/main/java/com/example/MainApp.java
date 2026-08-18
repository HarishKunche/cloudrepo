package com.example;

import com.sun.net.httpserver.HttpServer;
import io.prometheus.metrics.core.metrics.Counter;
import io.prometheus.metrics.core.metrics.Histogram;
import io.prometheus.metrics.expositionformats.PrometheusTextFormatWriter;
import io.prometheus.metrics.instrumentation.jvm.JvmMetrics;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;

public class MainApp {

    // Existing Request Counter
    private static final Counter httpRequestsTotal = Counter.builder()
            .name("http_requests_total")
            .help("Total number of incoming HTTP requests.")
            .labelNames("path", "method", "status")
            .register();

    // Response Time Histogram with explicit latency buckets (in seconds)
    private static final Histogram httpRequestDurationSeconds = Histogram.builder()
            .name("http_request_duration_seconds")
            .help("HTTP request response latencies in seconds.")
            .labelNames("path", "method", "status")
            .classicUpperBounds(0.005, 0.01, 0.025, 0.05, 0.1, 0.25, 0.5, 1.0)
            .register();

    public static void main(String[] args) throws Exception {
        JvmMetrics.builder().register();

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Custom business GET endpoint 1: Hello
        server.createContext("/api/hello", e -> {
            long startTime = System.nanoTime(); // Start the timer

            try {
                long fakeDelay = (long) (Math.random() * 30);
                Thread.sleep(fakeDelay);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

            String resp = "{\"message\": \"Hello from Java 21 inside Kubernetes!\"}";
            e.getResponseHeaders().set("Content-Type", "application/json");
            e.sendResponseHeaders(200, resp.length());
            try (var os = e.getResponseBody()) { os.write(resp.getBytes()); }

            // Calculate duration in seconds
            long durationNano = System.nanoTime() - startTime;
            double durationSeconds = durationNano / 1_000_000_000.0;

            // Record metrics for /api/hello
            httpRequestsTotal.labelValues("/api/hello", "GET", "200").inc();
            httpRequestDurationSeconds.labelValues("/api/hello", "GET", "200").observe(durationSeconds);
        });

        // 🟢 NEW Custom business GET endpoint 2: Status
        server.createContext("/api/status", e -> {
            long startTime = System.nanoTime(); // Start the timer

            try {
                long fakeDelay = (long) (Math.random() * 15); // slightly faster fake delay
                Thread.sleep(fakeDelay);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

            String resp = "{\"status\": \"UP\", \"database\": \"CONNECTED\"}";
            e.getResponseHeaders().set("Content-Type", "application/json");
            e.sendResponseHeaders(200, resp.length());
            try (var os = e.getResponseBody()) { os.write(resp.getBytes()); }

            // Calculate duration in seconds
            long durationNano = System.nanoTime() - startTime;
            double durationSeconds = durationNano / 1_000_000_000.0;

            // Record metrics for /api/status
            httpRequestsTotal.labelValues("/api/status", "GET", "200").inc();
            httpRequestDurationSeconds.labelValues("/api/status", "GET", "200").observe(durationSeconds);
        });

        // Prometheus Exporter endpoint
        server.createContext("/metrics", e -> {
            PrometheusTextFormatWriter writer = new PrometheusTextFormatWriter(false);
            var snapshots = PrometheusRegistry.defaultRegistry.scrape();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            writer.write(out, snapshots);
            byte[] responseBytes = out.toByteArray();

            e.getResponseHeaders().set("Content-Type", "text/plain; version=0.0.4; charset=utf-8");
            e.sendResponseHeaders(200, responseBytes.length);
            try (var os = e.getResponseBody()) { os.write(responseBytes); }
        });

        server.start();
        System.out.println("Java 21 metrics server started successfully on port 8080");
    }
}

