FROM openjdk:21-jdk

# === 2단계: 애플리케이션 JAR 파일 복사 ===
# 빌드된 JAR 파일을 컨테이너 이미지 내부로 복사합니다.
# ARG로 JAR 파일 경로를 받아 유연하게 설정할 수 있도록 합니다.
ARG JAR_FILE=build/libs/*.jar
# <-- Gradle 기본 빌드 경로에 맞춤 (Maven은 target/*.jar)

# 'app.jar'라는 이름으로 컨테이너 내부의 /app 디렉토리에 복사합니다.
COPY ${JAR_FILE} /app/app.jar

# === 3단계: 컨테이너 실행 설정 ===
# 컨테이너 실행 시 작업 디렉토리를 설정합니다.
WORKDIR /app

# 애플리케이션이 사용할 포트 명시 (정보 제공 목적, 실제 포트 개방은 docker run에서)
EXPOSE 8080
# Spring Boot 기본 포트
# 컨테이너 시작 시 실행될 명령어 (애플리케이션 JAR 실행)
# 이 형식(exec 형식)으로 ENTRYPOINT를 설정하면 docker run 명령 뒤에 붙는 인자들이
# 자바 실행 명령의 인자로 전달됩니다. (프로필 활성화 시 사용)
ENTRYPOINT ["java", "-jar", "app.jar"]