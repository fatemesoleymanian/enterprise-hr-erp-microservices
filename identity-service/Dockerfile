FROM eclipse-temurin:21-jdk-alpine AS common-build
WORKDIR /common
COPY common-lib/mvnw common-lib/pom.xml ./
COPY common-lib/.mvn .mvn
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline
COPY common-lib/src src
RUN ./mvnw install -DskipTests

FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY --from=common-build /root/.m2 /root/.m2
COPY identity-service/mvnw identity-service/pom.xml ./
COPY identity-service/.mvn .mvn
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline
COPY identity-service/src src
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
