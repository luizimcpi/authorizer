# AUTHORIZER

## Requisites
[JAVA 8](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)
[Docker](https://www.docker.com/)

## How to execute tests
```
run inside project directory the following command

./gradlew test
```

## How to run application using docker
```
run inside project directory the following commands

1 -> ./gradlew clean build
2 -> docker build -t authorizer .
3 -> docker-compose up
```

## How to run application using gradle and local java installation
```
run inside project directory the following commands

./gradlew clean build
java -jar build/libs/authorizer-1.0.0-SNAPSHOT.jar
```

## How to use
```
Paste the stdin in the terminal that is running the app
Example: 
{ "account": { "activeCard": true, "availableLimit": 100 } }
{ "account": { "activeCard": true, "availableLimit": 300 } }

Quit: press CTRL + C
```

## Used Libs

[JACKSON](https://github.com/FasterXML/jackson) -
Used to parse stdin to specific objects

[JUNIT 5](https://junit.org/junit5/) -
Used in unit tests

[KTLINT](https://github.com/pinterest/ktlint) -
An anti-bikeshedding Kotlin linter with built-in formatter.