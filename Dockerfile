# 1. Base 이미지 설정 (가볍고 많이 쓰이는 alpine 또는 eclipse-temurin 사용)
FROM eclipse-temurin:21-jdk-alpine

# 2. 컨테이너 내 작업 디렉토리 설정
WORKDIR /app

# 3. 빌드된 JAR 파일을 컨테이너의 app.jar로 복사
# 주의: ./gradlew build를 통해 생성된 jar 파일 경로를 정확히 지정해야 합니다.
COPY build/libs/*SNAPSHOT.jar app.jar

# 4. 외부에 노출할 포트
EXPOSE 8080

# 5. 컨테이너가 켜질 때 실행할 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]