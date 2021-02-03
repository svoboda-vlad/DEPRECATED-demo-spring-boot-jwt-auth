# demo-spring-boot-jwt-auth

## REST API endpoints
http://localhost:8080/

unrestricted:
- GET "/test" (TestController)
- POST "/login" (LoginFilter)
- GET "/current-user" (CurrentUserController)
- GET "/h2-console/**"

restricted:
- GET + POST "/hello" (HelloController)

## Models

Test - id (int), text (String)
- GET: [{"id":1,"text":"test 123"}]

Hello - id (long), content (String, min = 1, max = 255)
- GET: [{"id":9,"content":"hello4"},{"id":10,"content":"hello4"}]
- POST: {"content": "hello4"}

User - username (String), password (String)
- no endpoint

UserInfo - returned from endpoint "/current-user"
- username (String)

## Database

H2 in-memory database + liquibase

Database tables:
- hello - id (int PRIMARY KEY), content (varchar(255) NOT NULL)

## Authentication

InMemoryUserDetailsManager + BCryptPasswordEncoder
- username: "user", password: "password"

Login endpoints:
- POST "/login"
{"username": "user", "password": "password"}

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
