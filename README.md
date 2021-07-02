# Demo - Spring Boot with JWT authentication (demo-spring-boot-jwt-auth)

## REST API Authentication

cURL (WINDOWS + LINUX) - username + password login:

```
curl -i http://localhost:8080/login -d "{\"username\": \"user1\", \"password\": \"pass123\"}"
```

cURL (WINDOWS + LINUX) - Google ID token login + automatic registration of a new user:

```
curl -i http://localhost:8080/google-login -d "{\"idToken\": \"abcdef\"}"
```

Returned JWT token:

```
Authorization: Bearer abcdef
```

## REST API endpoints
http://localhost:8080/

Example of POST request with JWT token:

cURL (WINDOWS + LINUX)

```
curl -i http://localhost:8080/currency-code -d "{\"id\":0,\"currencyCode\": \"EUR\",\"country\": \"EMU\",\"rateQty\":1}" -H "Content-Type: application/json" -H "Authorization: Bearer abcdef"
```

Response:

```
{"id":6,"currencyCode":"EUR","country":"EMU","rateQty":1}
```

unrestricted:
- POST "/login" (LoginFilter)
- POST "/google-login" (GoogleLoginFilter)
- POST "/register" (UserController)
- GET "/current-user" (UserController)

unrestricted, but not REST API:
- GET "/h2-console/**"
- GET "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html"

restricted:
- GET + POST "/currency-code" (CurrencyCodeController)
- GET "/currency-code/1" (CurrencyCodeController)
- GET + POST "/exchange-rate" (ExchangeRateController)
- GET "/exchange-rate/currency-code/1" (ExchangeRateController)
- GET "/exchange-rate/2021-04-15" (ExchangeRateController)
- POST "/update-user" (UserController)

Swagger / OpenAPI

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

Heroku: [https://demo-spring-boot-jwt-auth.herokuapp.com/swagger-ui.html](https://demo-spring-boot-jwt-auth.herokuapp.com/swagger-ui.html)

H2 console

[http://localhost:8080/h2-console](http://localhost:8080/h2-console)

Heroku: [https://demo-spring-boot-jwt-auth.herokuapp.com/h2-console](https://demo-spring-boot-jwt-auth.herokuapp.com/h2-console)

## Models

CurrencyCode - id (long), currencyCode (String, min = 1, max = 255), country (String, min = 1, max = 255), rateQty (int, positive)
- GET: [{"id": 1,"currencyCode": "EUR","country": "EMU","rateQty": 1},
{"id": 2,"currencyCode": "USD","country": "USA","rateQty": 1}]
- POST: {"currencyCode": "EUR","country": "EMU","rateQty": 1}

ExchangeRate - id (long), rateDate (LocalDate), rate (BigDecimal, positive), currencyCode (CurrencyCode)
- GET: [{"id": 1,"rateDate": "2021-04-15","rate": 25.94,"currencyCode": {"id": 1,"currencyCode": "EUR","country": "EMU","rateQty": 1}},{"id": 2,"rateDate": "2021-04-15","rate": 21.669,
"currencyCode": {"id": 2,"currencyCode": "USD","country": "USA","rateQty": 1}}]
- POST: {"rateDate": "2021-04-16","rate": 25.925,"currencyCode": {"id": 1}}

User - id (long), username (String, min = 1, max = 255), password (String, min = 60, max = 60), lastLoginDateTime (LocalDateTime), previousLoginDateTime (LocalDateTime), loginProvider (LoginProvider - enum - INTERNAL, GOOGLE), givenName (String, min = 1, max = 255), familyName (String, min = 1, max = 255)
- no endpoint
- parsed from endpoint POST "/login"

UserInfo - username (String, min = 1, max = 255), lastLoginDateTime (LocalDateTime), previousLoginDateTime (LocalDateTime), givenName (String, min = 1, max = 255), familyName (String, min = 1, max = 255)
- GET "/current-user": {"username": "user1","givenName": "User 1","familyName": "User 1","lastLoginDateTime": "2021-05-05T12:50:12.354751","previousLoginDateTime": "2021-05-05T12:50:12.354751","userRoles":[{"id":1,"role":{"id":1,"name":"ROLE_USER"}}]}
- POST "/update-user": {"username": "user1","givenName": "User 1","familyName": "User 1"}

UserRegister - username (String, min = 1, max = 255), password (String, min = 4, max = 100)
- POST "/register": {"username": "test","password": "test123", "givenName": "Test", "familyName": "Test"}

GoogleIdTokenEntity - idToken (String, min = 1, max = 2048)
- no endpoint
- parsed from endpoint POST "/google-login"

Role
- no endpoint - id (long), name (String, min = 1, max = 255)

UserRoles
- no endpoint - id (long), user (User), role (Role)

## Database

H2 in-memory database + liquibase

JDBC URL: "jdbc:h2:mem:testdb"

Database tables:
- currency_code - id (int PRIMARY KEY), currency_code (VARCHAR(255) NOT NULL UNIQUE), country (VARCHAR(255) NOT NULL), rate_qty (INT NOT NULL)
- exchange_rate - id (int PRIMARY KEY), rate_date (date NOT NULL), rate (DECIMAL(10,3) NOT NULL), currency_code_id (INT NOT NULL)
- user - id (int PRIMARY KEY), username (VARCHAR(255) NOT NULL UNIQUE), password (VARCHAR(255) NOT NULL), last_login_date_time (TIMESTAMP), previous_login_date_time (TIMESTAMP), login_provider(VARCHAR(255), given_name(VARCHAR(255), family_name(VARCHAR(255))
- user_roles - id (int PRIMARY KEY), user_id (int NOT NULL), role_id (int NOT NULL)
- role - id (int PRIMARY KEY), name (VARCHAR(255) NOT NULL UNIQUE) - default values: "ROLE_USER", "ROLE_ADMIN"

## Authentication

UserDetailsService + BCryptPasswordEncoder

Login endpoints:

- POST "/login"

{"username": "user1", "password": "pass123"}

- POST "/google-login"

{"idToken": "abcdef"}

SessionCreationPolicy.STATELESS

## Registration

- POST "/register"

{"username": "test", "password": "test", "givenName": "Test", "familyName": "Test"}

cURL (WINDOWS + LINUX):

```
curl -i http://localhost:8080/register -d "{\"username\": \"test\", \"password\": \"test123\", \"givenName\": \"Test\", \"familyName\": \"Test\"}" -H "Content-Type: application/json"
```

## Dependencies

compile scope (default):
- spring-boot-starter-web
- spring-boot-starter-security
- spring-boot-starter-data-jpa
- spring-boot-starter-validation
- jjwt-api
- liquibase-core
- jaxb-api
- springdoc-openapi-ui
- springdoc-openapi-security
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
- postgresql

## Properties
DEFAULT:

spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.xml

spring.profiles.active=dev

google.client.clientids=733460469950-9bsam7nba7ljgj7nmhu3td2mrlctvhet.apps.googleusercontent.com

DEV:

spring.h2.console.enabled=true

spring.h2.console.settings.web-allow-others=true

spring.datasource.generate-unique-name=false

INTEG:

spring.datasource.url=jdbc:postgresql://localhost:5432/homestead

spring.datasource.username=homestead

spring.datasource.password=secret

PROD:

spring.h2.console.enabled=false

## HEROKU Config Vars

SPRING_PROFILES_ACTIVE=prod

DATABASE_URL=...