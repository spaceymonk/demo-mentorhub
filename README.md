# Introduction

Demo project. MentorHub is a platform application which enables its users to find or to become mentors about their
expertise. Detailed information
is available as software design document and is available under `docs/` folder.


# Deployment

1. Run `mvn package -D maven.skip.test=true`.
2. Copy generated JAR file as `app.jar` into `docker/mentorhub-app/` folder.
3. Run `docker-compose up -d` inside `docker/` folder.
4. Connect to `localhost:8080` for the application.

**Note:** Do not forget to specify the host and port in environment variables for Elasticsearch, MongoDb and MailHog 
(or another SMTP server). See below table:

| Env. Variables       | Default Value        |
| ---------------------|--------------------- |
| `ELASTICSEARCH_HOST` | localhost            |
| `ELASTICSEARCH_PORT` | 9200                 |
| `MONGO_HOST`         | localhost            |
| `MONGO_PORT`         | 27017                |
| `SMTP_HOST`          | localhost            |
| `SMTP_PORT`          | 1025                 |


# Disclaimer

This project is my first Spring Boot experience along with Docker, MongoDB, Elasticsearch, JavaDoc, Thymeleaf, 
LDAP & OAuth2 authentication etc.
Long story short, you might -and will- spot some newbie mistakes, so *cheers*.
