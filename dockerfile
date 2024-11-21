FROM maven:3.8.5-openjdk-21 AS build
WORKDIR /Movie-Browsing-Catalogue
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:21-jdk-slim
WORKDIR /Movie-Browsing-Catalogue
COPY --from=build /Movie-Browsing-Catalogue/target/MBC-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
