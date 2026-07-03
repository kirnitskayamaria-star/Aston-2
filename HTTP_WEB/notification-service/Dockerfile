FROM bellsoft/liberica-openjdk-alpine-musl:17
RUN apk add --no-cache curl
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

