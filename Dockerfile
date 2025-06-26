# --- Estágio 1: Build da Aplicação ---

FROM maven:3.8.5-openjdk-17 AS build

WORKDIR /app
COPY pom.xml .

# Baixa todas as dependências do projeto.
RUN mvn dependency:go-offline

# Copia todo o código-fonte do projeto.
COPY src ./src

# Compila a aplicação e a empacota em um arquivo .jar.
# -DskipTests para não rodar os testes durante a construção da imagem.
RUN mvn package -DskipTests


# --- Estágio 2: Execução da Aplicação ---

FROM openjdk:17-jdk-slim

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta 8080, que é a porta padrão que o Spring Boot usa.
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]