# Aggregator service uses pg 13 database

### Starting from ide docerized db only

1) Prepare db (flyway can't handle data base creation it self):
```sh
docker pull postgres:13.3
docker run --name pg13 -p 5432:5432 -v ~/klix/data:/var/lib/postgresql/data -e POSTGRES_PASSWORD=klix -d postgres:13.3
docker exec pg13 psql -U postgres -c"CREATE DATABASE klix" postgres
```

2) Change the host of db in aggregator service config if needed:
```sh
  ...
  jdbc-url: jdbc:postgresql://localhost:5432/klix?currentSchema=applications
```

### Starting as a stanalone services with composer

1) Creating docker images:
```sh
cd aggregator/
mvn clean package
docker build -t aggregator .
```
```sh
cd client/
mvn clean package
docker build -t client .
```
2) Creating docker network:
```sh
docker network create --gateway 172.99.99.1 --subnet 172.99.99.0/24 klixnw
```
3) Prepare db (flyway can't handle data base creation it self):
```sh
docker pull postgres:13.3
docker run --name pg13 -p 5432:5432 -v ~/klix/data:/var/lib/postgresql/data -e POSTGRES_PASSWORD=klix -d postgres:13.3
docker exec pg13 psql -U postgres -c"CREATE DATABASE klix" postgres
docker container stop pg13
docker container rm pg13
```
4) Up services:
```sh
docker-compose up
```
5)  [http://172.99.99.102:8080/](http://172.99.99.102:8080/) 
