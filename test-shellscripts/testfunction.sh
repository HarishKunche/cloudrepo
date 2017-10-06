#!/bin/sh
# A simple script with a function...

add_a_user()
{
  USER=$1
  PASSWORD=$2
  shift; shift;
  # Having shifted twice, the rest is now comments ...
  echo "Adding user $USER ..."
  useradd  $USER
  passwd $USER $PASSWORD
  echo "Added user $USER  with pass $PASSWORD"
}

###
# Main body of script starts here
###
echo "Start of script..."
add_a_user $1 $2 
############## need to check this script as  of now not working!!!!#######



