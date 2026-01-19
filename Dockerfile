FROM maven:3.9.6-eclipse-temurin-17

WORKDIR /app

COPY Backend/pom.xml .
COPY Backend/src ./src

RUN mvn clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/Bank-Loan-Management-0.0.1-SNAPSHOT.jar"]
