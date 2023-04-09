FROM openjdk:11-slim

WORKDIR /var/app
COPY **/target/*.jar ./

CMD ["bash", "-c", "java -jar ${SERVICE}*.jar"]