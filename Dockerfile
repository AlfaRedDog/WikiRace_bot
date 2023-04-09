FROM openjdk:19-slim

WORKDIR /var/app
COPY **/target/*.jar ./

CMD ["bash", "-c", "java -jar ${SERVICE}*.jar"]