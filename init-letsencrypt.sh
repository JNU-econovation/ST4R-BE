#!/bin/bash

# .env 파일 로드 및 변수 확인
if [ -f .env ]; then
  export $(grep -v '^#' .env | xargs)
else
  echo "❌ .env 파일이 없습니다. .env 파일을 생성하고 BACKEND_SERVER_NAME과 LETSENCRYPT_EMAIL을 설정해주세요."
  exit 1
fi

if [ -z "$BACKEND_SERVER_NAME" ] || [ -z "$LETSENCRYPT_EMAIL" ]; then
  echo "❌ .env 파일에 BACKEND_SERVER_NAME 또는 LETSENCRYPT_EMAIL 변수가 설정되지 않았습니다."
  exit 1
fi

# 이미 실제 인증서가 있는지 확인
if [ -d "./ssl/letsencrypt/live/$BACKEND_SERVER_NAME" ]; then
  if [ -L "./ssl/letsencrypt/live/$BACKEND_SERVER_NAME/fullchain.pem" ]; then
    echo "✅ 이미 '$BACKEND_SERVER_NAME'에 대한 실제 인증서가 존재합니다. 초기화 스크립트를 종료합니다."
    exit 0
  fi
fi

echo "### Let's Encrypt를 위한 더미 인증서 생성 (Nginx 최초 실행용)..."
mkdir -p ./ssl/letsencrypt/live/$BACKEND_SERVER_NAME
openssl req -x509 -nodes -newkey rsa:2048 -days 1 \
    -keyout "./ssl/letsencrypt/live/$BACKEND_SERVER_NAME/privkey.pem" \
    -out "./ssl/letsencrypt/live/$BACKEND_SERVER_NAME/fullchain.pem" \
    -subj "/CN=localhost"

echo "### Nginx 컨테이너 빌드 및 시작 (더미 인증서 사용)..."
docker compose up --force-recreate -d nginx

echo "### Certbot으로 실제 인증서 발급 요청..."
docker compose run --rm certbot \
    certonly --webroot -w /var/www/certbot \
    --email $LETSENCRYPT_EMAIL \
    --agree-tos --no-eff-email \
    -d $BACKEND_SERVER_NAME --force-renewal

echo "### Nginx 컨테이너 재시작 (실제 인증서 적용)..."
docker compose restart nginx

echo "🎉 Let's Encrypt 초기 설정이 완료되었습니다! 이제 deploy.sh를 사용하여 배포할 수 있습니다."
