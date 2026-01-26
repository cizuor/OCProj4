# Étape 1 : Build de l'application (Maven + Java 21)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# On copie le pom.xml et les sources
COPY pom.xml .
COPY src ./src

# On compile et on génère le JAR (en sautant les tests pour gagner du temps au déploiement)
RUN mvn clean package -DskipTests

# Étape 2 : Runtime (On ne garde que le nécessaire pour exécuter)
FROM eclipse-temurin:21-jre
WORKDIR /app

# On récupère le JAR généré à l'étape précédente
COPY --from=build /app/target/*.jar app.jar

# Port exposé (celui de ton application.properties)
EXPOSE 9000

# Commande de lancement
ENTRYPOINT ["java", "-jar", "app.jar"]