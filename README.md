# Bikecounter Rue de la Loi.

This application contains the Swagger API for [bikecounter-client](https://github.com/carlosgeos/bikecounter-client). It is also supposed to fetch data every 10 minutes from the Brussels Open Datastore (using cron, Heroku scheduler etc).

Swagger API link: [https://bikecounter-api.herokuapp.com](https://bikecounter-api.herokuapp.com)

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

## Counter/public API problems

### Lack of precision in the counter

When checking the counter at the 59th minute of the hour, it will
sometimes say no bikes (0) have passed during that hour. It is
considering the following hour already. This is accounted for
(repaired) in the function
`bikecounter.repair/repair-null-records`.


## Running

```sh
$ lein ring server
```

It will install dependencies automatically and start the server on port 3000.

Run:

```sh
$ lein ring uberjar
```

To compile and deploy etc.
