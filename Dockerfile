FROM maven:3.6.3-jdk-11-openj9 AS MAVEN_BUILD_ENVIRONMENT

COPY pom.xml /tmp/
COPY src /tmp/src/
WORKDIR /tmp/

RUN mvn clean package --no-transfer-progress -DskipTests

FROM openjdk:11-jre

ENV TZ=Europe/Oslo
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

VOLUME /tmp
COPY --from=MAVEN_BUILD_ENVIRONMENT /tmp/target/dataservice-catalog.jar app.jar

RUN sh -c 'touch /app.jar'
CMD java -jar app.jar