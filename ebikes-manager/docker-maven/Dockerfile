FROM maven:3.9.9-eclipse-temurin-21 AS build  
WORKDIR /project
COPY . /project/
RUN mvn clean package -DskipTests
EXPOSE 9200
CMD ["mvn", "exec:java", "-Dexec.mainClass=sap.ass2.ebikes.EbikesManagerLauncher" ]