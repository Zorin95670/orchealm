#!/usr/bin/env bash

set -e

echo "Starting version checker..."

# Run once at startup
/usr/local/bin/check-versions.sh

echo "Starting cron..."

crond -f -l 8