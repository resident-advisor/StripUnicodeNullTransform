# StripUnicodeNullTransform SMT

Custom Single Message Transform fro Debezium that replaces all occurrences of the Unicode character `\u0000` (or its equivalent in hexadecimal, `\0x00`) with nothing in the specified fields of a Struct record.

## Install
The SMT is written in [Java](https://www.java.com/en/) and packaged using [Maven](https://maven.apache.org/), so ensure these are installed when working with this package.
```
brew install java
```
```
brew install maven
```

## Deploy

To deploy the SMT for usage in our Debezium connectors we need to package it up and update the connector plugins.

1. Update the package version in `pom.xml`
2. Run `make build` - this will create the jar in the `target` directory
3. Create a new release in the Github repo - name and tag it the version number of the new package
4. Upload the newly created jar file to the release
5. In your terminal, create a sha512sum for the jar by running `shasum -a 512 <path-to-jar-file>`
6. In the [strimzi-kafka-connect-cluster](https://github.com/resident-advisor/k8s-config/blob/e9703c73f29f4c7114b00a4cd14db4673b69cc65/staging/releases/strimzi-kafka-connect-cluster.yaml#L69-L71) release in K8s, update the plugin with the new link to the jar file from the Github release and the sha512sum generated above
7. Deploy K8s and it will now be using the updated SMT

## Usage in Connector
To use this transform in a Kafka Connect sink connector, you can specify the transforms and `transforms.stripNullChars.type` properties in the connector configuration file, like so:

```
transforms=stripNullChars
transforms.stripNullChars.type=io.github.residentadvisor.strip_unicode_null_transform.StripUnicodeNullTransform
```

## Test
The tests are written in `src/test`. It is recommended to use the [Test Runner for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-test) VS Code extension to run the tests.

