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
```
docker-compose up -d
mvn spring-boot:run -Dspring-boot.run.profiles=develop
```

Then in another terminal e.g.
```
curl http://localhost:8080/ping
curl http://localhost:8080/ready
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
