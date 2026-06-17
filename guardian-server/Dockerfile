FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml ./
COPY guardian-core/pom.xml guardian-core/pom.xml
COPY guardian-cli/pom.xml guardian-cli/pom.xml
COPY guardian-server/pom.xml guardian-server/pom.xml

COPY guardian-core/src guardian-core/src
COPY guardian-server/src guardian-server/src

RUN mvn -q -pl guardian-server -am -DskipTests package && cp guardian-server/target/guardian-server-*.jar /tmp/app.jar

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /tmp/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
