# syntax=docker/dockerfile:1

FROM openjdk:latest

WORKDIR CreamEngine

COPY target/CreamEngine-1.1-SNAPSHOT-jar-with-dependencies.jar CreamEngine.jar
COPY src/main/resources/data data

ENTRYPOINT java -jar CreamEngine.jar

