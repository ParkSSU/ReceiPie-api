# 1. 자바 17 이미지 기반
FROM openjdk:17

# 2. jar 파일 복사
COPY build/libs/receipie-api-0.0.1-SNAPSHOT.jar app.jar

# 3. jar 실행 명령
CMD ["java", "-jar", "app.jar"]

