#!/usr/bin/env bash
set -euo pipefail
APP_DIR="$HOME/app"
mkdir -p "$APP_DIR"

: "${PREPARE_ONLY:=false}" "${LOCAL_DB:=false}" "${DB_NAME:=edupress}" "${DB_USERNAME:=edupress}" "${DB_PASSWORD:=changeme}" "${SERVER_PORT:=8080}" "${DB_URL:=}" "${JWT_BASE64_SECRET:=devsecret}" || true
CURRENT_USER="$(id -un)"
ACTIVE_SVC=""

echo "[Remote] deploy start prepare_only=$PREPARE_ONLY local_db=$LOCAL_DB"

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

  sudo -iu postgres psql -tc "SELECT 1 FROM pg_roles WHERE rolname='${DB_USERNAME}'" | grep -q 1 || sudo -iu postgres psql -c "CREATE USER ${DB_USERNAME} WITH PASSWORD '${DB_PASSWORD}';"
  sudo -iu postgres psql -tc "SELECT 1 FROM pg_database WHERE datname='${DB_NAME}'" | grep -q 1 || sudo -iu postgres psql -c "CREATE DATABASE ${DB_NAME} OWNER ${DB_USERNAME};"
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
[ "$LOCAL_DB" = "true" ] && provision_pg || true
write_env
write_service

if [ "$PREPARE_ONLY" = "true" ]; then
  echo "[Remote] prepare_only done"
  exit 0
fi

if [ -f "$APP_DIR/edupress.jar" ]; then
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
else
  echo "[Remote] Missing JAR $APP_DIR/edupress.jar" >&2
  exit 1
fi
