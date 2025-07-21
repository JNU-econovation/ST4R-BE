FROM nginx:latest

# 필요한 도구(envsubst)를 설치합니다.
RUN apt-get update && apt-get install -y gettext-base

# 설정 템플릿을 복사할 디렉토리를 생성합니다.
RUN mkdir -p /etc/nginx/templates

# 로컬의 템플릿 파일을 컨테이너의 템플릿 디렉토리로 복사합니다.
COPY nginx.conf.template /etc/nginx/templates/nginx.conf.template

# 시작 스크립트를 컨테이너에 복사하고 실행 권한을 부여합니다.
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

# 컨테이너가 시작될 때 실행할 명령어로 시작 스크립트를 지정합니다.
CMD ["/entrypoint.sh"]
