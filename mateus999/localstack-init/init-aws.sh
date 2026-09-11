#!/bin/bash
set -e

DLQ_URL=$(awslocal sqs create-queue --queue-name transferencias-dlq --query 'QueueUrl' --output text)
DLQ_ARN=$(awslocal sqs get-queue-attributes --queue-url "$DLQ_URL" --attribute-names QueueArn --query 'Attributes.QueueArn' --output text)

awslocal sqs create-queue --queue-name transferencias-queue --attributes '{
  "RedrivePolicy": "{\"deadLetterTargetArn\":\"'"$DLQ_ARN"'\",\"maxReceiveCount\":\"3\"}",
  "VisibilityTimeout": "30"
}'

awslocal secretsmanager create-secret \
  --name sistemaaws/postgres \
  --secret-string "{\"username\":\"${DB_USERNAME}\",\"password\":\"${DB_PASSWORD}\"}"
