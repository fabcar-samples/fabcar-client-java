# fabcar-client-java

Java FabCar client sample

## Prereqs

## Build

The fabric-gateway-java sample can be built using the included gradle wrapper:

```
git clone https://github.com/fabcar-samples/fabcar-client-java.git
cd fabcar-client-java
./gradlew shadowJar
```

## Run

```
java -jar build/libs/fabcar.jar admin <connection_profile> <wallet>
```

It's also possible to run the sample with a certificate and private key using the following command:

```
java -jar build/libs/fabcar.jar admin <connection_profile> <certificate> <private_key>
```

## Service discovery

You may need to use the `-Dorg.hyperledger.fabric.sdk.service_discovery.as_localhost=true` option if there are connection problems, for example when using the [Fabric test network](https://hyperledger-fabric.readthedocs.io/en/release-2.2/test_network.html).

See [org.hyperledger.fabric.gateway.Gateway JavaDoc](https://hyperledger.github.io/fabric-gateway-java/release-2.2/org/hyperledger/fabric/gateway/Gateway.html) for more information.

## Example

To run the FabCar client with the Fabric test network, use

```
java -Dorg.hyperledger.fabric.sdk.service_discovery.as_localhost=true -jar build/libs/fabcar.jar $CORE_PEER_MSPCONFIGPATH/../../../connection-org1.json admin $CORE_PEER_MSPCONFIGPATH/signcerts/Admin@org1.example.com-cert.pem $CORE_PEER_MSPCONFIGPATH/keystore/priv_sk
```
