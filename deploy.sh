#!/bin/bash
set -e

echo "🔄 origin master branch pull 중..."
git pull origin master

echo "🔧 ./gradlew 권한 +x로 변경 중..."
chmod +x ./gradlew

echo "🔨 gradle로 Jar 빌드 중"
./gradlew clean build -x test

echo "🐳 컨테이너 내렸다가 다시 올리는 중"
docker compose down
docker compose up --build -d

echo "✅ 배포 완료 -> http://localhost:8080"
