FROM openjdk:21-jdk AS builder

WORKDIR /app

# 소스 코드 복사
COPY . .

# 빌드 실행 (메모리 제한 및 최적화)
CMD ["./gradlew", "clean", "build", "-x", "test"]

# 런타임 스테이지
FROM openjdk:21-jdk

WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

# JVM 최적화 옵션
ENTRYPOINT ["java", "-jar", "app.jar"]
