FROM openjdk:17-jdk-slim

# 빌드된 JAR 파일을 컨테이너로 복사
COPY build/libs/backend-0.0.1-SNAPSHOT.jar app.jar

# 애플리케이션이 사용하는 포트를 외부에 노출
EXPOSE 8080

# 컨테이너 시작 시 JAR 파일 실행
ENTRYPOINT ["java", "-jar", "app.jar"]