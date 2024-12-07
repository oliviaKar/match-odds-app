FROM amazoncorretto:17-alpine-jdk
WORKDIR /app
# Install Maven
RUN apk add --no-cache maven

COPY . .
RUN mvn clean install -DskipTests=true

ENTRYPOINT ["java","-jar","target/match-odds-app-1.0.0.jar"]
