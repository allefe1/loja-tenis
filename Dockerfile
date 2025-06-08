# Use Java 21 conforme especificado no projeto
FROM openjdk:21-jdk-slim

# Definir diretório de trabalho
WORKDIR /app

# Copiar o JAR da aplicação
COPY target/*.jar app.jar

# Expor porta 8080
EXPOSE 8080

# Executar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
