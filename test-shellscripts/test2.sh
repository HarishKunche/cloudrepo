#!/bin/sh/
while read no
do
 case $no in
      public)     echo network is public network!
      ;;
      priv)   echo network is priv!
      ;;
      *)  echo  your input is incorrect!
          exit 0
      ;;
  esac
done 
