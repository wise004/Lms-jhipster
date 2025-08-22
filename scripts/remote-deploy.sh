#!/usr/bin/env bash
set -euo pipefail
APP_DIR="$HOME/app"
mkdir -p "$APP_DIR"

: "${PREPARE_ONLY:=false}" "${LOCAL_DB:=false}" "${DB_NAME:=edupress}" "${DB_USERNAME:=edupress}" "${DB_PASSWORD:=changeme}" "${SERVER_PORT:=8081}" "${DB_URL:=}" "${JWT_BASE64_SECRET:=devsecret}" || true
CURRENT_USER="$(id -un)"
ACTIVE_SVC=""

echo "[Remote] deploy start prepare_only=$PREPARE_ONLY local_db=$LOCAL_DB"

# Decode base64 password if provided via DB_PASSWORD_B64 (to avoid shell quoting issues in workflow)
if [ -n "${DB_PASSWORD_B64:-}" ]; then
  if command -v base64 >/dev/null 2>&1; then
    set +e
    decoded=$(echo "$DB_PASSWORD_B64" | base64 -d 2>/dev/null)
    rc=$?
    set -e
    if [ $rc -eq 0 ] && [ -n "$decoded" ]; then
      DB_PASSWORD="$decoded"
      echo "[Secret] DB password decoded from base64 (length=${#DB_PASSWORD})"
    else
      echo "[Secret] Failed to base64-decode DB_PASSWORD_B64; falling back to literal" >&2
    fi
  fi
fi

if [ -z "$DB_PASSWORD" ]; then
  echo "[Secret] ERROR: DB_PASSWORD is empty. Aborting before provisioning." >&2
  exit 1
fi
echo "[Secret] DB password length=${#DB_PASSWORD} (value hidden)"

escape_sql_literal() {
  # Escape single quotes for SQL literal
  printf "%s" "$1" | sed "s/'/''/g"
}

ensure_java() {
  if command -v java >/dev/null 2>&1; then
    return 0
  fi
  if command -v apt-get >/dev/null 2>&1; then
    sudo apt-get update -y || true
    sudo apt-get install -y openjdk-21-jre-headless || sudo apt-get install -y openjdk-17-jre-headless || true
  elif command -v dnf >/dev/null 2>&1; then
    sudo dnf -y install java-21-openjdk-headless || sudo dnf -y install java-17-openjdk-headless || true
  elif command -v yum >/dev/null 2>&1; then
    sudo yum -y install java-21-openjdk-headless || sudo yum -y install java-17-openjdk-headless || true
  fi
  if ! command -v java >/dev/null 2>&1; then
    echo "[Java] fallback download"
    ARCH=$(uname -m); case "$ARCH" in x86_64|amd64) A=x64;; aarch64|arm64) A=aarch64;; *) A=x64;; esac
    TMP=$(mktemp /tmp/jdk.tgz.XXXX)
    curl -fsSL "https://api.adoptium.net/v3/binary/latest/21/ga/linux/${A}/jdk/hotspot/normal/eclipse" -o "$TMP" || true
    if [ -s "$TMP" ]; then
      sudo mkdir -p /opt/jdk
      sudo tar -C /opt/jdk -xzf "$TMP"
      DIR=$(sudo find /opt/jdk -maxdepth 1 -type d -name 'jdk-*' | head -n1)
      [ -n "$DIR" ] && sudo ln -sfn "$DIR" /opt/jdk-current && sudo ln -sfn /opt/jdk-current/bin/java /usr/bin/java || true
    fi
    rm -f "$TMP"
  fi
  command -v java >/dev/null 2>&1 || { echo "Java install failed" >&2; return 1; }
}

provision_pg() {
  echo "[DB] provisioning postgres"
  if ! command -v psql >/dev/null 2>&1; then
    if command -v dnf >/dev/null 2>&1; then
      sudo dnf -y install postgresql16-server postgresql16 || sudo dnf -y install postgresql15-server postgresql15 || sudo dnf -y install postgresql-server postgresql || true
    elif command -v yum >/dev/null 2>&1; then
      sudo yum -y install postgresql15-server postgresql || sudo yum -y install postgresql14-server postgresql || true
    elif command -v apt-get >/dev/null 2>&1; then
      sudo apt-get update -y || true
      sudo apt-get -y install postgresql || true
    fi
  fi

  DATA_DIR="/var/lib/pgsql/data"
  [ -d /var/lib/pgsql/16/data ] && DATA_DIR="/var/lib/pgsql/16/data"
  [ -d /var/lib/pgsql/15/data ] && DATA_DIR="/var/lib/pgsql/15/data"

  # Initialize only if PG_VERSION missing AND directory truly empty (checked with sudo)
  if command -v postgresql-setup >/dev/null 2>&1; then
    if [ -f "$DATA_DIR/PG_VERSION" ]; then
      echo "[DB] Existing PG cluster detected (PG_VERSION present)"
    else
      # capture listing (ignore permission errors by using sudo)
      if sudo sh -c "ls -A '$DATA_DIR'" 2>/dev/null | grep -q .; then
        echo "[DB] Data dir not empty and no PG_VERSION -> skipping initdb (no destructive action)"
      else
        echo "[DB] Running postgresql-setup initdb (fresh)"
        if ! sudo postgresql-setup --initdb; then
          echo "[DB] postgresql-setup initdb failed" >&2
        fi
      fi
    fi
  fi

  for SVC in postgresql postgresql-16 postgresql-15; do sudo systemctl enable --now "$SVC" 2>/dev/null && { ACTIVE_SVC="$SVC"; break; } || true; done
  [ -n "$ACTIVE_SVC" ] && echo "[DB] Active postgres service: $ACTIVE_SVC" || echo "[DB] WARNING: No postgres systemd service started"

  # Wait for server to accept connections
  if command -v psql >/dev/null 2>&1; then
    for i in 1 2 3 4 5; do
      sudo -iu postgres psql -c "SELECT 1" >/dev/null 2>&1 && break || { echo "[DB] waiting for postgres ($i)"; sleep 2; }
    done
  fi

  sudo -iu postgres psql -tc "SELECT 1 FROM pg_roles WHERE rolname='${DB_USERNAME}'" | grep -q 1 || sudo -iu postgres psql -c "CREATE USER ${DB_USERNAME} WITH PASSWORD '$(escape_sql_literal "$DB_PASSWORD")';"
  sudo -iu postgres psql -tc "SELECT 1 FROM pg_database WHERE datname='${DB_NAME}'" | grep -q 1 || sudo -iu postgres psql -c "CREATE DATABASE ${DB_NAME} OWNER ${DB_USERNAME};"
  # Always ensure password in case user existed without one
  sudo -iu postgres psql -c "ALTER USER ${DB_USERNAME} WITH PASSWORD '$(escape_sql_literal "$DB_PASSWORD")';" >/dev/null 2>&1 || true

  # Ensure pg_hba.conf allows password (md5) auth for local connections and our user
  local hba=""
  for c in "$DATA_DIR/pg_hba.conf" "/var/lib/pgsql/data/pg_hba.conf" "/var/lib/pgsql/16/data/pg_hba.conf" "/var/lib/pgsql/15/data/pg_hba.conf"; do
    if sudo test -f "$c"; then hba="$c"; break; fi
  done
  if [ -n "$hba" ]; then
    # Detect preferred auth method based on server password_encryption (scram-sha-256 or md5)
    AUTH_METHOD="md5"
    if sudo -iu postgres psql -Atqc "SHOW password_encryption;" 2>/dev/null | grep -qi 'scram'; then AUTH_METHOD="scram-sha-256"; fi
    echo "[DB] Adjusting authentication in $hba (ident/peer -> $AUTH_METHOD)"
    sudo cp "$hba" "$hba.bak" || true
    # Replace ident/peer for local/host lines with md5 (non-destructive backup above)
    sudo sed -i -E "s/^(local[[:space:]].*[[:space:]]+)(ident|peer|md5|scram-sha-256)\s*$/\1$AUTH_METHOD/Ig" "$hba"
    sudo sed -i -E "s/^(host[[:space:]].*[[:space:]]127\.0\.0\.1\/32[[:space:]]+)(ident|peer|md5|scram-sha-256)/\1$AUTH_METHOD/I" "$hba"
    sudo sed -i -E "s/^(host[[:space:]].*[[:space:]]::1\/128[[:space:]]+)(ident|peer|md5|scram-sha-256)/\1$AUTH_METHOD/I" "$hba"
    # Ensure explicit entries for app user at top if not present
    if ! sudo grep -q "host *${DB_NAME} *${DB_USERNAME} *127.0.0.1/32" "$hba"; then
      sudo sed -i "1ihost    ${DB_NAME}    ${DB_USERNAME}    127.0.0.1/32    $AUTH_METHOD" "$hba"
    fi
    if ! sudo grep -q "host *${DB_NAME} *${DB_USERNAME} *::1/128" "$hba"; then
      sudo sed -i "1ihost    ${DB_NAME}    ${DB_USERNAME}    ::1/128         $AUTH_METHOD" "$hba"
    fi
    if ! sudo grep -q "^local[[:space:]]+${DB_NAME}[[:space:]]+${DB_USERNAME}" "$hba"; then
      sudo sed -i "1ilocal   ${DB_NAME}   ${DB_USERNAME}                             $AUTH_METHOD" "$hba"
    fi
    sudo systemctl reload "$ACTIVE_SVC" 2>/dev/null || sudo systemctl restart "$ACTIVE_SVC" || true
  echo "[DB] pg_hba.conf (first 25 lines)"; sudo head -n 25 "$hba" || true
  else
    echo "[DB] Could not locate pg_hba.conf to adjust authentication" >&2
  fi
}

write_env() {
  local ds="$DB_URL"; if [ "$LOCAL_DB" = "true" ] || [ -z "$ds" ]; then ds="jdbc:postgresql://localhost:5432/${DB_NAME}?sslmode=disable"; fi
  local env_file="$APP_DIR/edupress.env"
  # If existing env file owned by root make it writable for current user
  if [ -f "$env_file" ] && [ ! -w "$env_file" ]; then sudo chown "$CURRENT_USER" "$env_file" || true; fi
  cat > "$env_file" <<ENV
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=${SERVER_PORT}
SPRING_DATASOURCE_URL=${ds}
SPRING_DATASOURCE_USERNAME=${DB_USERNAME}
SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD}
JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET=${JWT_BASE64_SECRET}
ENV
  chmod 600 "$env_file" || true
  echo "[Remote] Env file created/updated: $env_file"
  ls -l "$env_file" || true
}

write_service() {
  local svc=/etc/systemd/system/edupress.service
  local afterLine="After=network.target"
  local requiresLine=""
  if [ "$LOCAL_DB" = "true" ] && [ -n "$ACTIVE_SVC" ]; then
    local svc_name="$ACTIVE_SVC"; case $svc_name in *.service) ;; *) svc_name="$svc_name.service";; esac
    afterLine="After=network.target $svc_name"
    requiresLine="Requires=$svc_name"
  fi
  local jar_path="$APP_DIR/edupress.jar"
  local env_file="$APP_DIR/edupress.env"
  sudo bash -c "cat > $svc" <<UNIT
[Unit]
Description=Edupress Spring Boot Application
$afterLine
$requiresLine
Wants=network-online.target

[Service]
Type=simple
WorkingDirectory=$APP_DIR
EnvironmentFile=$env_file
ExecStart=/usr/bin/java -jar $jar_path
Restart=always
RestartSec=5
User=$CURRENT_USER
SuccessExitStatus=143

[Install]
WantedBy=multi-user.target
UNIT
  sudo systemctl daemon-reload
  sudo systemctl enable edupress || true
  echo "[Remote] Service unit installed (After/Requires lines):"
  grep -E '^(After|Requires|EnvironmentFile|ExecStart)' "$svc" || true
}

ensure_java
# Auto-detect local DB need: if not explicitly true but DB_URL empty or points to localhost
if [ "$LOCAL_DB" != "true" ]; then
  if [ -z "${DB_URL}" ] || echo "$DB_URL" | grep -qiE '^jdbc:postgresql://(localhost|127\.0\.0\.1)'; then
    echo "[DB] LOCAL_DB auto-enabled (detected local jdbc url / empty DB_URL)"
    LOCAL_DB=true
  fi
fi
[ "$LOCAL_DB" = "true" ] && provision_pg || true
write_env
write_service

# Show pg_hba.conf snippet for diagnostics after potential modification
if [ "$LOCAL_DB" = "true" ]; then
  for h in /var/lib/pgsql/data/pg_hba.conf /var/lib/pgsql/16/data/pg_hba.conf /var/lib/pgsql/15/data/pg_hba.conf; do
    if sudo test -f "$h"; then
      echo "[DB] Showing first 60 lines of $h"; sudo head -n 60 "$h" || true
      break
    fi
  done
fi

if [ "$PREPARE_ONLY" = "true" ]; then
  echo "[Remote] prepare_only done"
  exit 0
fi

if [ -f "$APP_DIR/edupress.jar" ]; then
  # If using local DB ensure the application credentials actually work; if not, repair password.
  # Determine if datasource is local
  DS_LOCAL=false
  if echo "${DB_URL}" | grep -qiE '^jdbc:postgresql://(localhost|127\.0\.0\.1)' || [ -z "${DB_URL}" ]; then DS_LOCAL=true; fi
  if [ "$LOCAL_DB" = "true" ] || [ "$DS_LOCAL" = "true" ]; then
    if ! PGPASSWORD="$DB_PASSWORD" psql -U "$DB_USERNAME" -h 127.0.0.1 -d "$DB_NAME" -c "select 1" >/dev/null 2>&1; then
      echo "[DB] Credential test failed for $DB_USERNAME@$DB_NAME. Resetting password to provided secret."
  sudo -iu postgres psql -c "ALTER USER ${DB_USERNAME} WITH PASSWORD '$(escape_sql_literal "$DB_PASSWORD")';" || echo "[DB] ALTER USER failed" >&2
      # retry test
      if PGPASSWORD="$DB_PASSWORD" psql -U "$DB_USERNAME" -h 127.0.0.1 -d "$DB_NAME" -c "select 1" >/dev/null 2>&1; then
        echo "[DB] Credential test passed after reset."
      else
        echo "[DB] Credential test still failing after reset (will likely cause app startup failure)." >&2
      fi
    else
      echo "[DB] Credential test passed for $DB_USERNAME before app start."
    fi
  fi

  echo "[Remote] Starting (or restarting) edupress.service (port $SERVER_PORT)"
  if ! sudo systemctl restart edupress 2>/dev/null; then sudo systemctl start edupress || true; fi
  # Wait up to 90s for health endpoint
  ATTEMPTS=30
  HEALTH_URL="http://127.0.0.1:${SERVER_PORT}/management/health/liveness"
  for i in $(seq 1 $ATTEMPTS); do
    if sudo systemctl is-active --quiet edupress; then
      if curl -fsS --max-time 2 "$HEALTH_URL" >/dev/null 2>&1; then
        echo "[Remote] Service healthy (liveness)"
        break
      fi
    else
      echo "[Remote] Service not active yet (attempt $i)";
    fi
    sleep 3
  done
  if ! sudo systemctl is-active --quiet edupress; then
    echo "[Remote] Service failed to reach active state" >&2
    sudo systemctl status edupress --no-pager || true
    sudo journalctl -n 120 -u edupress --no-pager || true
    exit 1
  fi
  echo "[Remote] Service active; checking listening ports for java"
  (ss -ltnp 2>/dev/null | grep java) || (netstat -tlnp 2>/dev/null | grep java) || true
  echo "[Remote] Curling local liveness endpoint: $HEALTH_URL"
  curl -fsS --max-time 2 "$HEALTH_URL" || true
else
  echo "[Remote] Missing JAR $APP_DIR/edupress.jar" >&2
  exit 1
fi
