FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -q -B -DskipTests dependency:go-offline

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -q -B -DskipTests package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseG1GC -Djava.security.egd=file:/dev/./urandom"
ENV SPRING_PROFILES_ACTIVE=local

COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 1600

HEALTHCHECK --interval=30s --timeout=3s --retries=5 CMD wget -qO- http://localhost:1600/actuator/health || exit 1

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
