# Spring Boot + SSE
Demo project shows simple usage of SSE (Server-Sent Events).

The project uses SSE to implement fake vote results viewer.

Votes are generated from server and pushed by server to clients.

## Requirements
- Java 15+ (to build the project)
- Docker 20.10.6
- Docker Compose 1.29.1
- Modern browser (tested in Mozilla 88.0 and Chrome 90.0.4430.93)

## Run demo
- Build the project (command: `./gradlew bootjar` (Linux / Mac) or `gradlew.bat` (Windows))
- Start docker containers (command: `docker-compose up -d`)
- Open site by url: http://localhost:8080

## Live demo
- https://example-spring-boot-sse.herokuapp.com

![demo](https://user-images.githubusercontent.com/27987608/118588603-e8158a00-b7c8-11eb-93ca-3e779be1c576.gif)
