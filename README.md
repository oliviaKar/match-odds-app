# Match Management REST API
Simple CRUD API to manage football & basketball matches and their odds, using Java, SpringBoot, Maven, JPA, PostgreSQL.
## Entities
There are two entities present in this API

Match
* id
* description
* match_date
* match_tiime
* team_a
* team_b
* sport (enum 1.Football, 2.Basketball)


MatchOdds
* id
* match_id
* specifier (enum ONE, TWO, X)
* odd

## Project Structure
* `db-postgres/`: contains the Dockerfile and the table creation scripts for the PostgreSQL image
* `app/`: contains the source code for the Web API along with the Dockerfile for building the application image
* `.env`: file containing the env vars for the DB credentials
* `docker-compose.yaml`: configuration file for running the multi-container application (app & db)
## Run the API Locally
In order to run this application locally you need to have Docker installed. Follow the [instructions](https://docs.docker.com/engine/install/)

Then, you need to clone the repository and run the `docker-compose` command
```
git clone https://github.com/oliviaKar/match-odds-app.git
cd match-odds-app
docker-compose up
```

Once the app has started successfully you can access the API documentation in http://localhost:8080/swagger-ui/index.html#/

There is also a sample [Postman collection](match-app.postman_collection.json) that you can use to test the API
