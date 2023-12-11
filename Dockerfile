FROM gradle:8.5.0-jdk17 AS build

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src

RUN gradle build --no-daemon -x test

FROM gcr.io/distroless/java17:nonroot

COPY --from=build /home/gradle/src/build/libs/*.jar /app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]