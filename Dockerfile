# syntax=docker/dockerfile:1

FROM openjdk:latest

WORKDIR CreamEngine

COPY target/CreamEngine-1.1-SNAPSHOT-jar-with-dependencies.jar CreamEngine.jar

ENTRYPOINT java -jar CreamEngine.jar

