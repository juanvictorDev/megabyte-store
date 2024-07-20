FROM amazoncorretto:21-alpine
WORKDIR /java
COPY megabyte.jar .
ENTRYPOINT [ "java", "-jar", "megabyte.jar" ]