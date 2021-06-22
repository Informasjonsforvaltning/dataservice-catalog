# dataservice-catalog

Backend for the data service registration.

## Requirements
- maven
- java 8
- docker
- docker-compose

## Run tests
Run tests with maven:
```
mvn verify
```

## Run locally
Start the application with docker-compose and contact it with maven.
```
docker-compose up -d

curl http://localhost:8080/ping
curl http://localhost:8080/ready
```

It is also possible to start the application with maven, this requires mongodb & rabbitmq from docker-compose to be running.
```
mvn spring-boot:run -Dspring-boot.run.profiles=develop
```

Then in another terminal e.g.
```
curl http://localhost:9080/ping
curl http://localhost:9080/ready
```

## Datastore
To inspect local MongoDB:
```
% docker-compose exec mongodb mongo
% use admin
% db.auth("admin","admin")
% use dataservice-catalog
% db.dataservices.find()
```
