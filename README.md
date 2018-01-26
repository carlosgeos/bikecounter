# Bikecounter Rue de la Loi.

This application shows how many bikes go through [Rue de la Loi](https://www.google.com/maps?hl=en&q=rue+de+la+loi) every hour, every day, every month...

## About Bruxelles Mobilité Open Data

This application uses the data from [this Brussels Open Data Store](http://opendatastore.brussels/fr/dataset/bike-counting-poles).

The GeoJSON resource works just fine.

## Fetching and saving data from the online counter

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

## Counter problems

### Lack of precision in the counter

When checking the counter at the 59th minute of the hour, it will
sometimes say no bikes (0) have passed during that hour. It is
considering the following hour already. This is accounted for
(repaired) in the function
`bikecounter.repair/repair-null-records`.

## Running the client locally

Two running processes are needed.

```sh
$ lein sass4clj auto
```

To generate the .css files from the .scss sources. And:

```sh
$ lein ring server
```

Your app should now be running at [http://localhost:3000](http://localhost:3000/).
