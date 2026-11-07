FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /workspace
COPY pom.xml .
COPY backend/src backend/src
RUN mvn -B package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/target/stock-management-system-1.0.0.jar app.jar
EXPOSE 8082
HEALTHCHECK --interval=20s --timeout=5s --start-period=45s --retries=5 CMD wget -qO- http://127.0.0.1:8082/actuator/health | grep -q '"status":"UP"' || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
