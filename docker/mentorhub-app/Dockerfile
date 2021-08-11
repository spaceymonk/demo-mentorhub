FROM openjdk:11
RUN mkdir /app
WORKDIR /app
COPY "app.jar" .
# WORKDIR /usr/src/myapp
# RUN javac Main.java
CMD ["java", "-jar", "app.jar"]