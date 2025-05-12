#!/bin/bash
set -e

echo "🔄 Pulling latest code..."
git pull origin master

echo "🐳 Building and starting containers..."
docker compose down   # 기존 컨테이너 정리 (선택)
docker compose up --build -d

echo "✅ Deployment complete! App is running on http://localhost:8080"
