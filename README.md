# AUTHORIZER

## Requisites
[JAVA 8](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)
[DOCKER](https://docs.docker.com/get-docker/)

## Solving gradlew permission denied problem
```
run in a terminal inside project directory the following command

chmod +x gradlew
```

## How to execute tests
```
run in a terminal inside project directory the following command

./gradlew test

This task will run all unit tests and generates a html report from jacoco coverage task in located at:
/build/reports/jacoco/test/html/index.html can open with browser to see details

Current unit test coverage is 90%
The tasks are configured at build.gradle file
```

## How to run the application
```
run in a terminal inside project directory the following commands

./gradlew clean build
docker build -t authorizer .
docker run -i -t authorizer
```

## How to use
```
Paste the stdin in the terminal that is running the app
Example: 
{ "account": { "activeCard": true, "availableLimit": 100 } }
{ "account": { "activeCard": true, "availableLimit": 300 } }
{ "transaction": { "merchant": "Burger King", "amount": 20, "time": "2019-02-13T10:00:00.000Z" } }

Always put an a new empty line ("\n") when there are more than 1 line in stdin
Quit: press CTRL + C
```

## Used Libs

[JACKSON](https://github.com/FasterXML/jackson) -
Used to parse stdin to specific objects

[JUNIT 5](https://junit.org/junit5/) -
Used in unit tests

[KTLINT](https://github.com/pinterest/ktlint) -
An anti-bikeshedding Kotlin linter with built-in formatter.