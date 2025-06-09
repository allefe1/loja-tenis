# Usar OpenJDK 21 slim para menor tamanho
FROM openjdk:21-jdk-slim

# Definir diretório de trabalho
WORKDIR /app

# Copiar o JAR da aplicação
COPY target/*.jar app.jar

# Expor a porta que o Render espera
EXPOSE 10000

# Configurar variáveis de ambiente padrão
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=10000

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
