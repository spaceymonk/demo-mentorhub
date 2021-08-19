# Introduction

Demo project. MentorHub is a platform application which enables it’s users to find or to become mentors about their expertise. Detailed information
is available as software design document and is available under `docs/` folder.


# Deployment

1. Run `mvn package -D maven.skip.test=true`.
2. Copy generated JAR file as `app.jar` into `docker/mentorhub-app/` folder.
3. Run `docker-compose up -d` inside `docker/` folder.
4. Connect to `localhost:8080` for the application.

**Note:** If you want to run the application without Docker don't forget to change the addresses of the connections like `mongo:27017` to `<host>:<port>`.
There will be 3 such connections: MongoDB, Elasticsearch, Mailhog.


# Disclamer

This project is my first Spring Boot experience along with Docker, MongoDB, Elasticsearch, JavaDoc, Thymeleaf, LDAP & OAuth2 authentication etc.
Long story short, you might -and will- spot some newby mistakes, so *cheers*.
