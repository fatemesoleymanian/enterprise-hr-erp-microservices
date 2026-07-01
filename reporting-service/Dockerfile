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
COPY reporting-service/mvnw reporting-service/pom.xml ./
COPY reporting-service/.mvn .mvn
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline
COPY reporting-service/src src
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]
