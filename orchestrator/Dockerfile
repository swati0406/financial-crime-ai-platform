FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy root pom + orchestrator pom for dependency caching
COPY pom.xml .
COPY orchestrator/pom.xml orchestrator/

RUN mvn dependency:go-offline -pl orchestrator -am -q

# Copy source and build
COPY orchestrator/src orchestrator/src
RUN mvn package -pl orchestrator -am -DskipTests -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/orchestrator/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]