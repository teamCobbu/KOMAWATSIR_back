# 1. 베이스 이미지로 OpenJDK 사용
FROM openjdk:17-jdk-slim

# 2. JAR 파일을 Docker 이미지에 복사
COPY build/libs/komawatsir-0.0.1-SNAPSHOT.jar app.jar

# 3. 애플리케이션 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]
