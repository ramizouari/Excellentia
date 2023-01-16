#!/bin/sh
if [ -z "$ACCOUNT_NAME" ]; then
    echo "Please give an account name" 2>&1
    exit 1
fi;


for image in excellentia-common excellentia-runner excellentia-judge excellentia-compiler; do
    docker tag $image "$ACCOUNT_NAME"/"$image"
    docker push "$ACCOUNT_NAME"/"$image"
done