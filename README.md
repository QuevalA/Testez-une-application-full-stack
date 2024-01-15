# Yoga App

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 14.1.0 and [Spring Boot](https://spring.io/projects/spring-boot) version 2.6.1.

## Start the project
Git clone:
> git clone https://github.com/QuevalA/Testez-une-application-full-stack

<br>

### MySQL 5
SQL script for creating the schema is available `ressources/sql/script.sql`

Create the schema (ensure the specified MySQL user has the necessary privileges):
> mysql -u your_username -p your_password < ressources/sql/script.sql

By default, admin account for the app is:
- login: yoga@studio.com
- password: test!1234

<br>

### Back-end
Go inside folder:
> cd back

Install dependencies:
> mvn clean install

Launch Back-end:
> mvn spring-boot:run;

<br>

### Front-end
Go inside folder:
> cd front

Install dependencies:
> npm install

Launch Front-end:
> npm run start;
> 
<br>

### Environment Variables
Create the following files and include the specified variables.

back/.env

DATABASE_URL

DATABASE_USERNAME

DATABASE_PASSWORD

<br>
src/test/resources/test.properties 

TEST_ADMIN_USER_EMAIL

TEST_ADMIN_USER_PASSWORD

<br>

## Ressources

### Postman collection

For Postman import the collection

> ressources/postman/yoga.postman_collection.json

by following the documentation:

https://learning.postman.com/docs/getting-started/importing-and-exporting-data/#importing-data-into-postman

<br>

## Test

### Back end

To launch and generate the jacoco code coverage:
> mvn clean test

Report is available here:
> back/target/site/jacoco/index.html

![coverage-report -jacoco.jpg](ressources%2Fimages%2Fcoverage-report%20-jacoco.jpg)

<br>

### Front end

##### Unitary test

Launching test:
> npm run test

for following change:
> npm run test:watch

Report is available here:
> front/coverage/jest/lcov-report/index.html

![coverage-report -jest.jpg](ressources%2Fimages%2Fcoverage-report%20-jest.jpg)

<br>

##### E2E

Launching e2e test:
> npm run e2e

Generate coverage report (you should launch e2e test before):
> npm run e2e:coverage

Report is available here:
> front/coverage/lcov-report/index.html

![coverage-report -e2e.jpg](ressources%2Fimages%2Fcoverage-report%20-e2e.jpg)