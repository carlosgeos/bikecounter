# Bikecounter Rue de la Loi.

This application shows how many bikes go through [Rue de la Loi](https://www.google.com/maps?hl=en&q=rue+de+la+loi) every hour, every day, every month...

## About Bruxelles Mobilité Open Data

This application uses the data from [this Brussels Open Data Store](http://opendatastore.brussels/fr/dataset/bike-counting-poles).

The GeoJSON resource works just fine. The CSV does not give the time in UTC (minor disadvantage).

## Fetching and saving data from the online counter to populate a database

Have the environment variables:

```
{:dev {:env {:db-name ""
             :db-type ""
             :db-host ""
             :db-user ""
             :db-password ""}}}
```
OR
```
DB_NAME     = x
DB_TYPE     = x
DB_HOST     = x
DB_USER     = x
DB_PASSWORD = x
```

pointing to the database and:

```
$ lein run -m bikecounter.fetch
```

This is currently done using a cron job.

## Counter/public API problems

### Lack of precision in the counter

When checking the counter at the 59th minute of the hour, it will
sometimes say no bikes (0) have passed during that hour. It is
considering the following hour already. This is accounted for
(repaired) in the function
`bikecounter.repair/repair-null-records`.

## Running the client locally

It is only a static HTML file generated with Webpack


```sh
$ npm i
```

Then, simply run

```sh
$ npx webpack-dev-server
```

And the app should now be running at [http://localhost:8080](http://localhost:8080/).

## Running the backend server (Swagger API endpoints)

```sh
$ lein install
```

```sh
$ lein ring server
```
