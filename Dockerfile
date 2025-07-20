FROM openjdk:21-jdk

# 빌드된 JAR 파일을 컨테이너 이미지 내부로 복사
ARG JAR_FILE=build/libs/*.jar

# 'app.jar'라는 이름으로 컨테이너 내부의 /app 디렉토리에 복사
COPY ${JAR_FILE} /app/app.jar

# 컨테이너 실행 시 작업 디렉토리를 설정
WORKDIR /app

# 환경 변수 파일을 컨테이너 내부로 복사
COPY .env .

# 애플리케이션이 사용할 포트
EXPOSE 8080

# 컨테이너 시작 시 실행될 명령어 (애플리케이션 JAR 실행)
ENTRYPOINT ["java", "-jar", "app.jar"]