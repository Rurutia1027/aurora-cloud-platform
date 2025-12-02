#!/bin/bash
set -e

# Maven build

for module in customer fraud notification apigw; do
  echo "Building $module ..."
  cd $module
  mvn clean package -DskipTests=false
  cd ../
done

# Docker build
for module in customer fraud notification apigw; do
  echo "Building Docker image for $module ..."
  docker build -t nanachi1027/aurora-$module:latest ./$module
done

# List images
docker images | grep aurora | grep nanachi1027

# Exit here for now
exit

# TODO: Docker login
echo "$DOCKER_HUB_PASSWORD" | docker login -u "$DOCKER_HUB_USERNAME" --password-stdin

# Push images
for module in customer fraud notification apigw; do
  docker push nanachi1027/aurora-$module:latest
done

