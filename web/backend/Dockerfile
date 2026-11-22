# ---------- Build ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /src
# Tải thư viện trước để Docker cache lại khi chỉ đổi mã nguồn
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
# Test chạy ở CI (cần Docker cho Testcontainers), ở đây chỉ đóng gói
RUN mvn -B -q package -DskipTests

# ---------- Run ----------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S app && adduser -S app -G app
COPY --from=build /src/target/qlcuahang-web.jar app.jar
USER app
ENV TZ=Asia/Ho_Chi_Minh \
    JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseSerialGC -Xss512k"
EXPOSE 8080
HEALTHCHECK --interval=15s --timeout=3s --start-period=60s --retries=5 \
  CMD wget -qO- "http://127.0.0.1:${PORT:-8080}/actuator/health" | grep -q UP || exit 1
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
