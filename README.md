# Brussels Bikecounter.

This application contains the API for
[bikecounter-client](https://github.com/carlosgeos/bikecounter-client). It
is also supposed to fetch data every 10 minutes from the Brussels Open
Datastore.

## About Bruxelles Mobilité Open Data

This application uses the data from [this Brussels Open Data Store](http://opendatastore.brussels/fr/dataset/bike-counting-poles).

The GeoJSON resource works just fine. The CSV does not give the time in UTC.

## Fetching and saving data from the online counter to populate a database

Have the environment variable:

```
{:dev {:env {:database-url "mongodb://...."}}}
```

in the `profiles.clj` file.

OR
```
DATABASE_URL = x
```

as an env variable pointing to the Mongo database and:

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

To compile and deploy etc.
```sh
$ lein uberjar
```
