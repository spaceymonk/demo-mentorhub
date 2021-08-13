FROM openjdk:11
RUN mkdir /app
WORKDIR /app
# after building with `mvn package` replace with app.jar
COPY "app.jar" .
CMD ["java", "-jar", "app.jar"]