#!/bin/sh
host="$1"
port="$2"

echo "Waiting for $host:$port to become available..."

while ! nc -z "$host" "$port"; do
  sleep 2
done

echo "$host:$port is up. Starting service..."
shift 2
exec "$@"