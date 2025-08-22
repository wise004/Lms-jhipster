#!/usr/bin/env bash
set -euo pipefail
APP_DIR="$HOME/app"
mkdir -p "$APP_DIR"

: "${PREPARE_ONLY:=false}" "${LOCAL_DB:=false}" "${DB_NAME:=edupress}" "${DB_USERNAME:=edupress}" "${DB_PASSWORD:=changeme}" "${SERVER_PORT:=8081}" "${DB_URL:=}" "${JWT_BASE64_SECRET:=devsecret}" || true

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
      sudo dnf -y install postgresql15-server postgresql15 || sudo dnf -y install postgresql16-server postgresql16 || sudo dnf -y install postgresql-server postgresql || true
    elif command -v yum >/dev/null 2>&1; then
      sudo yum -y install postgresql14-server postgresql || true
    elif command -v apt-get >/dev/null 2>&1; then
      sudo apt-get update -y || true
      sudo apt-get -y install postgresql || true
    fi
  fi
  if command -v postgresql-setup >/dev/null 2>&1; then sudo postgresql-setup --initdb || true; fi
  for SVC in postgresql postgresql-15 postgresql-16; do sudo systemctl enable --now "$SVC" 2>/dev/null || true; done
  sudo -iu postgres psql -tc "SELECT 1 FROM pg_roles WHERE rolname='${DB_USERNAME}'" | grep -q 1 || sudo -iu postgres psql -c "CREATE USER ${DB_USERNAME} WITH PASSWORD '${DB_PASSWORD}';"
  sudo -iu postgres psql -tc "SELECT 1 FROM pg_database WHERE datname='${DB_NAME}'" | grep -q 1 || sudo -iu postgres psql -c "CREATE DATABASE ${DB_NAME} OWNER ${DB_USERNAME};"
}

write_env() {
  local ds="$DB_URL"; if [ "$LOCAL_DB" = "true" ] || [ -z "$ds" ]; then ds="jdbc:postgresql://localhost:5432/${DB_NAME}?sslmode=disable"; fi
  cat > "$APP_DIR/edupress.env" <<ENV
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=${SERVER_PORT}
SPRING_DATASOURCE_URL=${ds}
SPRING_DATASOURCE_USERNAME=${DB_USERNAME}
SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD}
JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET=${JWT_BASE64_SECRET}
ENV
}

write_service() {
  local svc=/etc/systemd/system/edupress.service
  sudo bash -c "cat > $svc" <<'UNIT'
[Unit]
Description=Edupress Spring Boot Application
After=network.target

[Service]
Type=simple
WorkingDirectory=%h/app
EnvironmentFile=%h/app/edupress.env
ExecStart=/usr/bin/env bash -c 'set -a; source %h/app/edupress.env; exec java -jar %h/app/edupress.jar'
Restart=always
RestartSec=5
User=%u

[Install]
WantedBy=multi-user.target
UNIT
  sudo systemctl daemon-reload
  sudo systemctl enable edupress || true
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
  sudo systemctl restart edupress || sudo systemctl start edupress
  sleep 2
  sudo systemctl status edupress --no-pager || true
else
  echo "[Remote] Missing JAR $APP_DIR/edupress.jar" >&2
  exit 1
fi
