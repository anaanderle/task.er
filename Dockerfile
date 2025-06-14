# Etapa de build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa de execução
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /build/target/*.jar app.jar
COPY src/main/resources/credentials.json ./credentials.json

EXPOSE 8080
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]
