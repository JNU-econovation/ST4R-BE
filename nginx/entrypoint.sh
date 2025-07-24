#!/bin/sh
set -e

CONF_PATH="/etc/nginx/nginx.conf"
TEMPLATE_PATH="/etc/nginx/templates/nginx.conf.template"
CERT_PATH="/etc/letsencrypt/live/${BACKEND_SERVER_NAME}/fullchain.pem"

HTTPS_BLOCK=$(cat <<EOF
server {
  listen 443 ssl http2;
  server_name ${BACKEND_SERVER_NAME};

  ssl_certificate /etc/letsencrypt/live/${BACKEND_SERVER_NAME}/fullchain.pem;
  ssl_certificate_key /etc/letsencrypt/live/${BACKEND_SERVER_NAME}/privkey.pem;
  ssl_protocols TLSv1.2 TLSv1.3;
  ssl_prefer_server_ciphers on;

  location /websocket/connect {
    proxy_pass http://spring:8080;

    # --- 웹소켓 필수 헤더 설정 ---
    proxy_http_version 1.1;
    proxy_set_header Upgrade \$http_upgrade;
    proxy_set_header Connection "Upgrade";

    # --- 프록시 관련 기본 헤더 설정 ---
    proxy_set_header Host \$http_host;
    proxy_set_header X-Real-IP \$remote_addr;
    proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto \$scheme; # HTTPS 통신임을 백엔드에 알림
  }


  location / {
    proxy_pass http://spring:8080;
    proxy_set_header X-Real-IP \$remote_addr;
    proxy_set_header Host \$http_host;
    proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto \$scheme;
  }
}
EOF
)

if [ -f "$CERT_PATH" ]; then
  echo "✅ 인증서 발견, HTTPS 서버 블록 삽입"
  awk -v block="$HTTPS_BLOCK" '
    {
      if ($0 ~ /### HTTPS_SERVERS ###/) {
        print block
      } else {
        print
      }
    }
  ' "$TEMPLATE_PATH" > "$CONF_PATH"
else
  echo "❗ 인증서 없음, HTTPS 서버 블록 미삽입"
  awk '
    $0 !~ /### HTTPS_SERVERS ###/ { print }
  ' "$TEMPLATE_PATH" > "$CONF_PATH"
fi

exec nginx -g 'daemon off;'