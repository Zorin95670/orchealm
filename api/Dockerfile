FROM maven:3.9.16-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Maven repository kept in a BuildKit cache: dependencies are not downloaded again on each build.
RUN --mount=type=cache,target=/root/.m2 mvn -B -q clean package -DskipTests

FROM eclipse-temurin:25-jre
RUN groupadd --system orchealm && useradd --system --gid orchealm --no-create-home orchealm
WORKDIR /app
COPY --from=build /app/target/*.jar orchealm-api.jar
USER orchealm
EXPOSE 8443
ENTRYPOINT ["java", "-jar", "orchealm-api.jar"]
