#!/bin/sh
# shutdown-k8s-cluster.sh

CLUSTER_NAME="k8s-cluster-aurora"

kind get clusters
kind delete cluster --name "$CLUSTER_NAME"