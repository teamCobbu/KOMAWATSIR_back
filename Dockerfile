# 1. 베이스 이미지로 OpenJDK 사용
FROM openjdk:17-jdk-slim

# 2. 시스템 폰트 및 필수 패키지 설치
RUN apt-get update && apt-get install -y \
    fontconfig \
    fonts-dejavu \
    fonts-noto \
    && fc-cache -fv \
    && apt-get clean

# 3. 폰트 리소스 폴더를 Docker 이미지에 복사
COPY src/main/resources/fonts /app/fonts

# 4. JAR 파일을 Docker 이미지에 복사
COPY build/libs/komawatsir-0.0.1-SNAPSHOT.jar app.jar

# 5. 작업 디렉토리 설정
WORKDIR /app

# 6. 애플리케이션 실행 명령어 (headless 모드 활성화)
ENTRYPOINT ["java", "-Djava.awt.headless=true", "-jar", "app.jar"]
