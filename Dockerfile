# syntax=docker/dockerfile:1

FROM openjdk:latest

WORKDIR CreamEngine

COPY .mvn/ .mvn
COPY mvnw pom.xml ./

RUN ./mvnw dependency:go-offline

COPY src ./src

CMD ["./mvnw", "clean", "install", "exec:java", "-Dexec.mainClass=org.incelexit.creamengine.Main"]

