# https://hub.docker.com/_/postgres/
docker run --name openchs-database -p 127.0.0.1:5432:5432 -e POSTGRES_PASSWORD=password -d openchs-database