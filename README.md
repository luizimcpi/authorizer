# AUTHORIZER

## Requisites
[JAVA 8](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)

## How to execute tests
```
run inside project directory the following command

./gradlew test
```

## How to run application
```
run inside project directory the following commands

./gradlew clean build
java -jar build/libs/authorizer-1.0.0-SNAPSHOT.jar
```

## Used Libs

[JACKSON](https://github.com/FasterXML/jackson) -
Used to parse stdin to specific objects

[JUNIT 5](https://junit.org/junit5/) -
Used in unit tests

[KTLINT](https://github.com/pinterest/ktlint) -
An anti-bikeshedding Kotlin linter with built-in formatter.