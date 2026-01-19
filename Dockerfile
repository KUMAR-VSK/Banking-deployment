# ================= BUILD STAGE =================
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

COPY Backend/pom.xml .
COPY Backend/src ./src
RUN mvn dependency:go-offline

RUN mvn clean package -DskipTests

# ================= RUNTIME STAGE =================
FROM eclipse-temurin:17-jre

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
