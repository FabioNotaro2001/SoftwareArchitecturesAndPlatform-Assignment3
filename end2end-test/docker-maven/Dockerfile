FROM maven:3.9.9-eclipse-temurin-21 AS build  
WORKDIR /project
COPY . /project/

RUN mvn clean package -DskipTests


# Comando per eseguire i test
CMD ["mvn", "test"]