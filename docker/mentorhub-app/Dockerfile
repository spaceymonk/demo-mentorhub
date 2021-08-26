FROM openjdk:11
RUN mkdir /config
WORKDIR /config
COPY wait-for-it.sh .
RUN chmod +x /config/wait-for-it.sh
# after building with `mvn package` replace with app.jar
RUN mkdir /app
WORKDIR /app
COPY "app.jar" .
ENV ELASTICSEARCH_HOST=elasticsearch
ENV MONGO_HOST=mongo
ENV SMTP_HOST=mailhog
CMD [ "bash", "-c", "/config/wait-for-it.sh mongo:27017 && /config/wait-for-it.sh elasticsearch:9200 -- java -jar /app/app.jar" ]