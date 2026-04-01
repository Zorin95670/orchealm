#!/bin/sh
set -e

if [ -n "$1" ]; then
  if [ -f "$1" ]; then
    echo "📄 Loading env from $1"
    set -a
    . "$1"
    set +a
  else
    echo "❌ Env file '$1' not found"
    exit 1
  fi
else
  echo "ℹ️ No env file provided, using system environment variables"
fi

: "${KEYCLOAK_ADMIN_USERNAME:?KEYCLOAK_ADMIN_USERNAME is required}"
: "${KEYCLOAK_ADMIN_PASSWORD:?KEYCLOAK_ADMIN_PASSWORD is required}"

: "${KEYCLOAK_ADMIN_TOKEN_URL:?KEYCLOAK_ADMIN_TOKEN_URL is required}"
: "${KEYCLOAK_ADMIN_CLI_URL:?KEYCLOAK_ADMIN_CLI_URL is required}"
: "${KEYCLOAK_ADMIN_CLIENT_ID:?KEYCLOAK_ADMIN_CLIENT_ID is required}"

: "${KEYCLOAK_USERS_API_URL:?KEYCLOAK_USERS_API_URL is required}"
: "${KEYCLOAK_HEALTH_URL:?KEYCLOAK_HEALTH_URL is required}"

echo "⏳ Waiting for Keycloak to be ready..."
TIMEOUT=120
ELAPSED=0
SLEEP_INTERVAL=2

until curl -s "$KEYCLOAK_HEALTH_URL" -o /dev/null; do
  if [ "$ELAPSED" -ge "$TIMEOUT" ]; then
    echo "❌ Keycloak did not start after $TIMEOUT seconds"
    exit 1
  fi
  sleep "$SLEEP_INTERVAL"
  ELAPSED=$((ELAPSED + SLEEP_INTERVAL))
done

echo "✅ Keycloak is up"

# --- Reset password for keycloak users ---
TOKEN=$(curl -s -X POST "$KEYCLOAK_ADMIN_CLI_URL" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=$KEYCLOAK_ADMIN_USERNAME" \
  -d "password=$KEYCLOAK_ADMIN_PASSWORD" \
  -d "grant_type=password" \
  -d "client_id=$KEYCLOAK_ADMIN_CLI_CLIENT_ID" \
  | sed -n 's/.*"access_token":"\([^"]*\)".*/\1/p')

if [ -z "$TOKEN" ] || [ "$TOKEN" = "null" ]; then
  echo "❌ Failed to retrieve access token"
  exit 1
fi

echo "✅ Token retrieved"
usernames="team1 team2 viewer bot"

for username in $usernames; do
  echo "🔎 Start reset password for: $username"

  USER_ID=$(curl -s -X GET "$KEYCLOAK_USERS_API_URL?username=$username" \
    -H "Authorization: Bearer $TOKEN" | sed -n 's/.*"id":"\([^"]*\)".*/\1/p')

  if [ -z "$USER_ID" ] || [ "$USER_ID" = "null" ]; then
    echo "⚠️ Unknown '$username' user"
    continue
  fi

  curl -s -X PUT "$KEYCLOAK_USERS_API_URL/$USER_ID/reset-password" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d "{
      \"type\": \"password\",
      \"value\": \"password\",
      \"temporary\": false
    }"

  echo "✅ Password reset for $username"
done
