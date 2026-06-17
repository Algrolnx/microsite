FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .

RUN mvn dependency:go-offline
COPY src ./src

RUN mvn clean package

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=builder /app/target/worker-1.0-SNAPSHOT-jar-with-dependencies.jar ./worker.jar

CMD sleep 5 && java -jar worker.jar