FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy root pom + ALL module poms
COPY pom.xml .
COPY orchestrator/pom.xml orchestrator/
COPY customer-service/pom.xml customer-service/
COPY fraud-service/pom.xml fraud-service/
COPY payment-service/pom.xml payment-service/
COPY sanctions-service/pom.xml sanctions-service/
COPY audit-service/pom.xml audit-service/

RUN mvn dependency:go-offline -pl orchestrator -am -q

# Copy only orchestrator source and build
COPY orchestrator/src orchestrator/src
RUN mvn package -pl orchestrator -am -DskipTests -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/orchestrator/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]