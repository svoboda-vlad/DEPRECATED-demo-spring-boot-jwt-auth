# Demo - Spring Boot with JWT authentication (demo-spring-boot-jwt-auth)

## REST API endpoints
http://localhost:8080/

unrestricted:
- GET "/hello" (HelloController)
- POST "/login" (LoginFilter)
- GET "/current-user" (CurrentUserController)

unrestricted, but not REST API:
- GET "/h2-console/**"

restricted:
- GET "/hello-restricted" (HelloController)
- GET + POST "/note" (NoteController)

Swagger / OpenAPI

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Models

Hello - text (String)
- GET: [{"text":"Hello World!"}]

Note - id (long), content (String, min = 1, max = 255)
- GET: [{"id":9,"content":"hello4"},{"id":10,"content":"hello4"}]
- POST: {"content": "hello4"}

User - username (String), password (String)
- no endpoint

UserInfo - username (String)
- GET: {"username":"user"}
- returned from endpoint "/current-user"

## Database

H2 in-memory database + liquibase

Database tables:
- note - id (int PRIMARY KEY), content (varchar(255) NOT NULL)

CommandLineRunner - default notes (note 123, note 124)

## Authentication

InMemoryUserDetailsManager + BCryptPasswordEncoder
- username: "user", password: "password"

Login endpoints:

- POST "/login"

{"username": "user", "password": "password"}

cURL: 

WINDOWS

```
curl -i -d "{\"username\": \"user\", \"password\": \"password\"}" http://localhost:8080/login
```

LINUX

```
curl -i -d '{"username": "user", "password": "password"}' http://localhost:8080/login
```

SessionCreationPolicy.STATELESS

## Dependencies

compile scope (default):
- spring-boot-starter-web
- spring-boot-starter-security
- spring-boot-starter-data-jpa
- spring-boot-starter-validation
- jjwt-api
- liquibase-core
- jaxb-api

provided scope:
- lombok

test scope:
- spring-boot-starter-test
- spring-security-test

runtime scope:
- jjwt-impl
- jjwt-jackson
- h2

## Properties

spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.xml

spring.h2.console.enabled=true

spring.h2.console.settings.web-allow-others=true

spring.datasource.generate-unique-name=false
