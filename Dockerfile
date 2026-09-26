FROM eclipse-temurin:25-jdk-jammy AS build

WORKDIR /workspace

COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw
RUN ./mvnw -B dependency:go-offline

COPY src src
RUN ./mvnw -B -DskipTests package \
    && cp target/*SNAPSHOT.jar app.jar

FROM eclipse-temurin:25-jre-jammy

RUN useradd --system --create-home --shell /usr/sbin/nologin appuser
WORKDIR /app

COPY --from=build /workspace/app.jar app.jar

USER appuser
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]