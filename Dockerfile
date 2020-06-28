FROM java:8
WORKDIR /
ADD ./build/libs/authorizer-1.0.0-SNAPSHOT.jar authorizer.jar
CMD java -jar authorizer.jar