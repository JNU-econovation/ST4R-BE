#!/bin/bash
set -e

echo "🔄 origin master branch pull 중..."
git pull origin master

echo "🔧 ./gradlew 권한 +x로 변경 중..."
chmod +x ./gradlew

echo "🔧 /nginx/entrypoint.sh 권한 +x로 변경 중..."
chmod +x ./nginx/entrypoint.sh

echo "🔨 gradle로 Jar 빌드 중"
./gradlew clean build -x test

echo "🐳 컨테이너 내렸다가 다시 올리는 중"
docker compose down
docker compose up --build -d

# .env 파일에서 도메인 이름을 읽어와서 완료 메시지에 사용합니다.
# 만약 .env 파일이 없다면 기본값으로 localhost를 사용합니다.
if [ -f .env ]; then
  # .env 파일에서 BACKEND_SERVER_NAME 값을 추출합니다.
  # 등호(=) 뒤의 모든 문자를 가져옵니다.
  export $(cat .env | grep -v '^#' | xargs)
fi

SERVER_URL="https://""${BACKEND_SERVER_NAME:-localhost}"

echo "✅ 배포 완료 -> ${SERVER_URL}"