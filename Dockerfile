# Etapa de build: usa o OpenJDK 21 para compilar o projeto com Maven Wrapper
FROM openjdk:21-jdk AS build
WORKDIR /app

# Copiar arquivos de configuração do Maven
COPY pom.xml .
COPY src src
COPY mvnw .
COPY .mvn .mvn

# Dar permissão de execução ao mvnw
RUN chmod +x ./mvnw

# Compilar o projeto usando Maven Wrapper
RUN ./mvnw clean package -DskipTests

# Etapa final: cria a imagem definitiva com JRE slim
FROM openjdk:21-jdk-slim

# Criar volume temporário
VOLUME /tmp

# Copiar o JAR compilado da etapa de build
COPY --from=build /app/target/*.jar app.jar

# Definir o ponto de entrada da aplicação
ENTRYPOINT ["java", "-jar", "/app.jar"]

# Expor a porta 8080 (padrão do Spring Boot)
EXPOSE 8080
