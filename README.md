# fabcar-client-java

Java FabCar client sample

## Prereqs

## Build

The fabric-gateway-java sample can be built using the included gradle wrapper:

```
git clone https://github.com/fabcar-samples/fabcar-client-java.git
cd fabcar-client-java
./gradlew shadow
```

## Run

```
java -jar build/libs/simpleFabricClient.jar admin <connection_profile> <wallet>
```

It's also possible to run the sample with a certificate and private key using the following command:

```
java -jar build/libs/simpleFabricClient.jar admin <connection_profile> <certificate> <private_key>
```
