# demo-spring-boot-jwt-auth
# demo-spring-boot-jwt-auth
branches:
1) "h2-inmemoryauth"
2) "master"
## A. branch "h2-inmemoryauth"

## 1) REST API endpoints:
http://localhost:8080/

unrestricted:
- GET "/test" (TestController)
- POST "/login" (LoginFilter)
- POST "/login-google" (LoginGoogleFilter)

(- "/h2-console/**")

restricted:
- GET + POST "/hello" (HelloController)
- POST "/verify" (GoogleTokenController)

## 2) Models:

Hello - id (long), content (String, min = 1, max = 255)
- GET: [{"id":9,"content":"hello4"},{"id":10,"content":"hello4"}]
- POST: {"content": "hello4"}

Test - id (int), text (String)
- GET: [{"id":1,"text":"test 123"}]

User - givenName (String), familyName (String), sub (String)
- returned from endpoint: POST "/verify"

IdToken - idToken (String)
- endpoint: POST "/verify"
- GET: not implemented
- POST: {"idToken": "eyabcdef"}

UserCredentials - username (String), password (String)
- no endpoint

## 3) Database

H2 in-memory DB + liquibase

DB tables:
- hello - id (int PRIMARY KEY), content (varchar(255) NOT NULL)

## 4) Authentication

InMemoryUserDetailsManager + BCryptPasswordEncoder
- username: "user", password: "password"
- username: "108564931079495851483", password: ""

Login endpoints:
- POST "/login"
{"username": "user", "password": "password"}
- POST "/login-google"
{"idToken": "eyabcdef"}

## 5) Dependencies

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

## 6) Properties

spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.xml
spring.h2.console.enabled=true
spring.h2.console.settings.web-allow-others=true
spring.datasource.generate-unique-name=false
google.client.clientids=733460469950-84s81fm32dvqku5js9rvlf6llqekr6l4.apps.googleusercontent.com

## B. branch "master"
## ADDED compared to "h2-inmemoryauth"
## 1) REST API endpoints:

unrestricted:
GET "/current-user" (CurrentUserController)

## 2) Models:

User - no endpoint, used for authentication
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

## 3) Database

DB tables: user
- id (int PRIMARY KEY)
- username (varchar(255) NOT NULL)
- email (varchar(255) NOT NULL)
- given_name (varchar(255) NOT NULL)
- family_name (varchar(255) NOT NULL)
- locale (varchar(255) NOT NULL)
- last_login_date_time (timestamp)

## 4) Authentication:

UserDetailsService + NoOpPasswordEncoder
UserService, UserRepository

CommandLineRunner - default users
username: "108564931079495851483"

Login endpoints:
- POST "/login-google"
{"idToken": "eyabcdef"}

LoginGoogleFilter - updateLastLoginDateTime

SessionCreationPolicy.STATELESS

## REMOVED compared to "h2-inmemoryauth"

## 1) REST API endpoints:

unrestricted: POST "/login" (LoginFilter)
restricted: POST "/verify" (GoogleTokenController)

## 2) Models:

User - givenName (String), familyName (String), sub (String)
- returned from endpoint: POST "/verify"
UserCredentials - username (String), password (String)
- no endpoint
