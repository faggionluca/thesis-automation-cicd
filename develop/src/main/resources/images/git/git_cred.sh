#!/bin/sh
sleep 10
# set cache as the credential helper and its timeout
git config --global credential.helper 'cache --timeout 86400'

# confirm setting
git config credential.helper

# add credentials to the cache, e.g. those set in environment variables.
# with a PAT the username can be anything except blank
<< eof tr -d ' ' | git credential-cache store 
  protocol=https
  host=$1
  username=$2
  password=$3
eof

# query the cache
<< eof tr -d ' ' | git credential-cache get
  protocol=https
  host=$1
eof

git clone $4 /repo