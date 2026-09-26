#!/bin/sh
set -eu

DLQ_URL=$(awslocal sqs create-queue --queue-name transferencias-dlq --query QueueUrl --output text)
DLQ_ARN=$(awslocal sqs get-queue-attributes \
  --queue-url "$DLQ_URL" \
  --attribute-names QueueArn \
  --query 'Attributes.QueueArn' \
  --output text)

MAIN_URL=$(awslocal sqs create-queue --queue-name transferencias --query QueueUrl --output text)

ATTRS=$(printf '{"RedrivePolicy":"{\\"deadLetterTargetArn\\":\\"%s\\",\\"maxReceiveCount\\":\\"5\\"}","VisibilityTimeout":"30"}' "$DLQ_ARN")
awslocal sqs set-queue-attributes --queue-url "$MAIN_URL" --attributes "$ATTRS"

echo "SQS criada: transferencias"
echo "DLQ criada: transferencias-dlq"
