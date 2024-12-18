# 1. 베이스 이미지 설정
FROM openjdk:17-jdk-slim

# 2. 시스템 폰트 및 필수 패키지 설치
RUN apt-get update && apt-get install -y \
    fontconfig \
    fonts-dejavu \
    fonts-noto \
    && fc-cache -fv \
    && apt-get clean

# 3. 작업 디렉토리 생성 및 설정
WORKDIR /app

# 4. 폰트 리소스 복사
COPY src/main/resources/fonts ./fonts

# 5. JAR 파일 복사
COPY build/libs/komawatsir-0.0.1-SNAPSHOT.jar ./app.jar

# 6. JAR 파일 권한 설정 (필요시)
RUN chmod +x ./app.jar

# 7. 애플리케이션 실행
ENTRYPOINT ["java", "-Djava.awt.headless=true", "-jar", "./app.jar"]