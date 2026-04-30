FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="Linggoutong Team"

VOLUME /tmp

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 9000

ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
