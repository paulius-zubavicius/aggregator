#
# Package stage
#
FROM openjdk:11-jre
CMD mkdir -p /usr/web/app/logs
COPY target/aggregator.jar /usr/web/app/app.jar
WORKDIR /usr/web/app
EXPOSE 8090
ENTRYPOINT ["java","-jar","app.jar"]