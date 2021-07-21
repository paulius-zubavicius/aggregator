#
# Package stage
#
FROM openjdk:11-jre
CMD mkdir -p /usr/web/app/logs
COPY target/client.jar /usr/web/app/app.jar
WORKDIR /usr/web/app
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]