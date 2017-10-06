#!/bin/sh
read name
echo i am printing name here :: $name

for i in `cat /root/data.txt`
do
echo looping numbers is  $i
done

echo "creating the file from variable called name"
mkdir  ${name}_file

