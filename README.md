# Demo - Spring Boot with JWT authentication (demo-spring-boot-jwt-auth)

## REST API endpoints
http://localhost:8080/

unrestricted:
- POST "/login" (LoginFilter)
- POST "/register" (RegistrationController)
- GET "/current-user" (CurrentUserController)

unrestricted, but not REST API:
- GET "/h2-console/**"

restricted:
- GET + POST "/currency-code" (CurrencyCodeController)
- GET "/currency-code/1" (CurrencyCodeController)
- GET + POST "/exchange-rate" (ExchangeRateController)
- GET "/exchange-rate/currency-code/1" (ExchangeRateController)
- GET "/exchange-rate/2021-04-15" (ExchangeRateController)

Swagger / OpenAPI

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

Heroku: [https://demo-spring-boot-jwt-auth.herokuapp.com/swagger-ui.html](https://demo-spring-boot-jwt-auth.herokuapp.com/swagger-ui.html)

H2 console

[http://localhost:8080/h2-console](http://localhost:8080/h2-console)

Heroku: [https://demo-spring-boot-jwt-auth.herokuapp.com/h2-console](https://demo-spring-boot-jwt-auth.herokuapp.com/h2-console)

## Models

CurrencyCode - id (long), currencyCode (String, min = 1, max = 255), country (String, min = 1, max = 255), rateQty (int, positive)
- GET: [{"id": 1,"currencyCode": "EUR","country": "EMU","rateQty": 1,"exchangeRates": [{"id": 1,"rateDate": "2021-04-15","rate": 25.94}]},
{"id": 2,"currencyCode": "USD","country": "USA","rateQty": 1,"exchangeRates": [{"id": 2,"rateDate": "2021-04-15","rate": 21.669}]}]
- POST: {"currencyCode": "EUR","country": "EMU","rateQty": 1}

ExchangeRate - id (long), rateDate (LocalDate), rate (BigDecimal, positive), currencyCode (CurrencyCode)
- GET: [{"id": 1,"rateDate": "2021-04-15","rate": 25.94,"currencyCode": {"id": 1,"currencyCode": "EUR","country": "EMU","rateQty": 1}},{"id": 2,"rateDate": "2021-04-15","rate": 21.669,
"currencyCode": {"id": 2,"currencyCode": "USD","country": "USA","rateQty": 1}}]
- POST: {"rateDate": "2021-04-16","rate": 25.925,"currencyCode": {"id": 1}}

User - id (long), username (String, min = 1, max = 50), password (String, min = 60, max = 60), lastLoginDateTime (LocalDateTime), previousLoginDateTime (LocalDateTime)
- no endpoint

CurrentUser - username (String), lastLoginDateTime (LocalDateTime), previousLoginDateTime (LocalDateTime)
- GET: {"username": "user","lastLoginDateTime": "2021-05-05T12:50:12.354751","previousLoginDateTime": "2021-05-05T12:50:12.354751"}
- returned from endpoint "/current-user"

RegistrationUser - username (String, min = 1, max = 50), password (String, min = 4, max = 100)
- POST: {"username": "test","password": "test123"}

## Database

H2 in-memory database + liquibase

Database tables:
- currency_code - id (int PRIMARY KEY), currency_code (VARCHAR(255) NOT NULL UNIQUE), country (VARCHAR(255) NOT NULL), rate_qty (INT NOT NULL)
- exchange_rate - id (int PRIMARY KEY), rate_date (date NOT NULL), rate (DECIMAL(10,3) NOT NULL), currency_code_id (INT NOT NULL)
- user - id (int PRIMARY KEY), username (VARCHAR(255) NOT NULL UNIQUE), password (VARCHAR(255) NOT NULL), last_login_date_time (TIMESTAMP), previous_login_date_time (TIMESTAMP)

CommandLineRunner - default currency codes (EUR, USD), default exchange rates (15.4.2021), default user: (username: "user", password "password")

## Authentication

UserDetailsService + BCryptPasswordEncoder
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

## Registration

- POST "/register"

{"username": "test", "password": "test"}

cURL: 

WINDOWS

```
curl -i -d "{\"username\": \"test\", \"password\": \"test123\"}" http://localhost:8080/login
```

LINUX

```
curl -i -d '{"username": "test", "password": "test123"}' http://localhost:8080/login
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
