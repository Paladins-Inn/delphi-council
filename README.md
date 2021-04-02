# DELPHI-COUNCIL - the torganized play configuration

> What man is a man who does not make the world better.
>
> -- Balian, Kingdom of Heaven

![Dependabot](https://flat.badgen.net/dependabot/Paladins-Inn/delphi-council/?icon=dependabot)
![Maven](https://github.com/Paladins-Inn/delphi-council/workflows/Java%20CI%20with%20Maven/badge.svg)
[![Docker Repository on Quay](https://quay.io/repository/klenkes74/delphi-council/status "Docker Repository on Quay")](https://quay.io/repository/klenkes74/kp-rpg-discord-bot)

## License
The license for the software is LGPL 3.0 or newer. Parts of the software may be licensed under other licences like MIT
or Apache 2.0 - the files are marked appropriately.

## Architecture

tl;dr (ok, only the bullshit bingo words):
- Immutable Objects (where frameworks allow)
- Relying heavily on generated code
- 100 % test coverage of human generated code
- Every line of code not written is bug free!

## Distribution
The software is distributed via quay.io. You find the images as

- quay.io/klenkes74/delphi-council:0.1.0 (bleeding edge)

The images are prepared for consumption by OpenShift 3.11, so they run without any problems on kubernetes, too.


## Development notes

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

### Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

### Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

### Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/delphi-council-0.1.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.html.

### Related guides

- Qute Templating ([guide](https://quarkus.io/guides/qute)): Offer templating support for web, email, etc in a build time, type-safe way
- SmallRye Fault Tolerance ([guide](https://quarkus.io/guides/microprofile-fault-tolerance)): Define fault-tolerant services
- Scheduler - tasks ([guide](https://quarkus.io/guides/scheduler)): Schedule jobs and tasks
- RESTEasy JAX-RS ([guide](https://quarkus.io/guides/rest-json)): REST endpoint framework implementing JAX-RS and more
- Keycloak Authorization ([guide](https://quarkus.io/guides/security-keycloak-authorization)): Policy enforcer using Keycloak-managed permissions to control access to protected resources
- Narayana JTA - Transaction manager ([guide](https://quarkus.io/guides/transaction)): Offer JTA transaction support (included in Hibernate ORM)
- SmallRye OpenAPI ([guide](https://quarkus.io/guides/openapi-swaggerui)): Document your REST APIs with OpenAPI - comes with Swagger UI
- Hibernate ORM with Panache ([guide](https://quarkus.io/guides/hibernate-orm-panache)): Simplify your persistence code for Hibernate ORM via the active record or the repository pattern
- Undertow WebSockets ([guide](https://quarkus.io/guides/websockets)): WebSocket communication channel support
- OpenID Connect Token Propagation ([guide](https://quarkus.io/guides/security-openid-connect-client)): Use JAX-RS client filter to propagate the current access token as HTTP Authorization Bearer value
- Agroal - Database connection pool ([guide](https://quarkus.io/guides/datasource)): Pool JDBC database connections (included in Hibernate ORM)
- Mailer ([guide](https://quarkus.io/guides/mailer)): Send emails
- Hibernate Envers ([guide](https://quarkus.io/guides/hibernate-orm#envers)): Enable Hibernate Envers capabilities in your JPA applications
- OpenShift ([guide](https://quarkus.io/guides/openshift)): Generate OpenShift resources from annotations
- MongoDB with Panache ([guide](https://quarkus.io/guides/mongodb-panache)): Simplify your persistence code for MongoDB via the active record or the repository pattern
- REST Client ([guide](https://quarkus.io/guides/rest-client)): Call REST services
- OpenID Connect Client Filter ([guide](https://quarkus.io/guides/security-openid-connect-client)): Use JAX-RS client filter to get and refresh the access tokens and set them as HTTP Authorization Bearer values
- RESTEasy Multipart ([guide](https://quarkus.io/guides/rest-json#multipart-support)): Multipart support for RESTEasy
- YAML Configuration ([guide](https://quarkus.io/guides/config#yaml)): Use YAML to configure your Quarkus application
- Logging JSON ([guide](https://quarkus.io/guides/logging#json-logging)): Add JSON formatter for console logging
- SmallRye Health ([guide](https://quarkus.io/guides/microprofile-health)): Monitor service health
- Micrometer metrics ([guide](https://quarkus.io/guides/micrometer-metrics)): Instrument the runtime and your application with dimensional metrics using Micrometer.
- SmallRye OpenTracing ([guide](https://quarkus.io/guides/opentracing)): Trace your services with SmallRye OpenTracing
- Hibernate Validator ([guide](https://quarkus.io/guides/validation)): Validate object properties (field, getter) and method parameters for your beans (REST, CDI, JPA)
- OpenID Connect ([guide](https://quarkus.io/guides/security-openid-connect)): Secure your applications with OpenID Connect Adapter and IDP such as Keycloak
- Elytron Security Properties File ([guide](https://quarkus.io/guides/security-properties)): Secure your applications using properties files
- Liquibase ([guide](https://quarkus.io/guides/liquibase)): Handle your database schema migrations with Liquibase
- Cache ([guide](https://quarkus.io/guides/cache)): Enable application data caching in CDI beans

## Note from the author
This software is meant do be perfected not finished.

If someone is interested in getting it faster, we may team up. I'm open for that. But be warned: I want to do it
_right_. So no short cuts to get faster. And be prepared for some basic discussions about the architecture or software
design :-).

---
Bensheim, 2021-03-11