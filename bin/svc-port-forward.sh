#!/bin/bash

# PostgresSQL
kubectl port-forward -n aurora svc/postgres 5432:5432

# RabbitMQ（AMQP）
kubectl port-forward -n aurora svc/rabbitmq 5672:5672

# RabbitMQ Management UI
kubectl port-forward -n aurora svc/rabbitmq 15672:15672