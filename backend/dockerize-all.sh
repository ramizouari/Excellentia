#! /bin/sh
# This script is run before dockerization
# It is used to set up the common docker image
docker build -t excellentia-common .
for app in compiler runner judge; do
    ( cd $app && docker build -t "excellentia-$app" . )
done;