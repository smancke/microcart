
FROM registry.mancke.net/java-base


ADD front-service/target/microcart-front*.jar /microcart-front.jar
ADD front-service/config.yaml /config.yaml

EXPOSE 5003

CMD ["java", "-jar", "/microcart-front.jar", "server", "/config.yaml"]
