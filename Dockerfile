FROM eclipse-temurin:23-jdk AS builder
WORKDIR /app
COPY pom.xml .
COPY . .
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:23-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
COPY --from=builder /app/src/main/resources/out.json ./src/main/resources/out.json
EXPOSE 8087
ENTRYPOINT [ "java", "-jar", "app.jar" ]