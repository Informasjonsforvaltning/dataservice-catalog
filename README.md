# Data Service Catalog

This application provides an API for the management of data services. A data service is defined according to
the [DCAT-AP-NO](https://data.norge.no/specification/dcat-ap-no) specification.

For a broader understanding of the system’s context, refer to
the [architecture documentation](https://github.com/Informasjonsforvaltning/architecture-documentation) wiki. For more
specific context on this application, see the **Registration** subsystem section.

## Getting Started

These instructions will give you a copy of the project up and running on your local machine for development and testing
purposes.

### Prerequisites

Ensure you have the following installed:

- Java 21
- Maven
- Docker

### Running locally

Clone the repository

```sh
git clone https://github.com/Informasjonsforvaltning/dataservice-catalog.git
cd dataservice-catalog
```

Start MongoDB, RabbitMQ and the application (either through your IDE using the dev profile, or via CLI):

```sh
docker compose up -d
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Running tests

```sh
mvn verify
```


