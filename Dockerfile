# Etapa de build: usa Maven oficial com Eclipse Temurin OpenJDK 21 para compilar
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copiar pom.xml primeiro para cache de dependências
COPY pom.xml .

# Baixar dependências (cache layer)
RUN mvn dependency:go-offline -B

# Copiar código fonte
COPY src ./src

# Compilar aplicação
RUN mvn clean package -DskipTests -B

# Etapa final: cria a imagem definitiva
FROM openjdk:21-jdk-slim

# Criar volume temporário
VOLUME /tmp

# Copiar o JAR compilado da etapa de build
COPY --from=build /app/target/*.jar app.jar

# Definir o ponto de entrada da aplicação
ENTRYPOINT ["java", "-jar", "/app.jar"]

# Expor a porta 8080
EXPOSE 8080
