#!/bin/sh

# 'envsubst'는 템플릿 파일 내의 $변수들을 실제 환경 변수 값으로 치환해줍니다.
envsubst '${BACKEND_SERVER_NAME}' < /etc/nginx/templates/nginx.conf.template > /etc/nginx/nginx.conf

# Nginx를 foreground로 실행하여 컨테이너가 종료되지 않도록 합니다.
exec nginx -g 'daemon off;'