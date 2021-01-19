# demo-spring-boot-jwt-auth
Branches:
1) master
2) h2-inmemoryauth

## 1) master branch
differences compared to h2-inmemoryauth branch
## REST API endpoints

unrestricted:
GET "/current-user" (CurrentUserController)

## Models

User - no endpoint yet (TODO registration), used for authentication
- id (long)
- username (String, min = 1, max = 255)
- email (String, min = 1, max = 255)
- givenName (String, min = 1, max = 255)
- familyName (String, min = 1, max = 255)
- locale (String, min = 1, max = 255)
- lastLoginDateTime (LocalDateTime)

UserInfo - returned from endpoint "/current-user"
- username (String)
- lastLoginDateTime (LocalDateTime)

## Database

DB tables: user
- id (int PRIMARY KEY)
- username (varchar(255) NOT NULL)
- email (varchar(255) NOT NULL)
- given_name (varchar(255) NOT NULL)
- family_name (varchar(255) NOT NULL)
- locale (varchar(255) NOT NULL)
- last_login_date_time (timestamp)

## Authentication

UserDetailsService + NoOpPasswordEncoder - no passwords used
UserService, UserRepository

CommandLineRunner - default user
username: "108564931079495851483"

Login endpoints:
- POST "/login-google"
{"idToken": "eyabcdef"}

authentication only using valid ID token from Google for sub "108564931079495851483"

SessionCreationPolicy.STATELESS

LoginGoogleFilter - updateLastLoginDateTime()

## 2) h2-inmemoryauth branch

## REST API endpoints
http://localhost:8080/

unrestricted:
- GET "/test" (TestController)
- POST "/login" (LoginFilter) - NOT IN MASTER BRANCH
- POST "/login-google" (LoginGoogleFilter)
- GET "/h2-console/**"

restricted:
- GET + POST "/hello" (HelloController)
- POST "/verify" (GoogleTokenController) - NOT IN MASTER BRANCH

## Models

Test - id (int), text (String)
- GET: [{"id":1,"text":"test 123"}]

Hello - id (long), content (String, min = 1, max = 255)
- GET: [{"id":9,"content":"hello4"},{"id":10,"content":"hello4"}]
- POST: {"content": "hello4"}

IdToken - idToken (String)
- endpoint: POST "/verify"
- GET: not implemented
- POST: {"idToken": "eyabcdef"}

NOT IN MASTER BRANCH - User - givenName (String), familyName (String), sub (String)
- returned from endpoint: POST "/verify"

NOT IN MASTER BRANCH - UserCredentials - username (String), password (String)
- no endpoint

## Database

H2 in-memory database + liquibase

Database tables:
- hello - id (int PRIMARY KEY), content (varchar(255) NOT NULL)

## Authentication

InMemoryUserDetailsManager + BCryptPasswordEncoder
- username: "user", password: "password" - NOT IN MASTER BRANCH
- username: "108564931079495851483", password: ""

Login endpoints:
- POST "/login" - NOT IN MASTER BRANCH
{"username": "user", "password": "password"}

- POST "/login-google"
{"idToken": "eyabcdef"}

## Dependencies

compile scope (default):
- spring-boot-starter-web
- spring-boot-starter-security
- spring-boot-starter-data-jpa
- spring-boot-starter-validation
- jjwt-api
- liquibase-core
- jaxb-api
- google-api-client

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
google.client.clientids=733460469950-84s81fm32dvqku5js9rvlf6llqekr6l4.apps.googleusercontent.com
