
# Bikecounter Rue de la Loi.

This application shows how many bikes go through [Rue de la Loi](https://www.google.com/maps?hl=en&q=rue+de+la+loi) every hour, every day, every month...

This application uses the data from [this Brussels Open Data Store](http://opendatastore.brussels/fr/dataset/bike-counting-poles).

## Running Locally

Two running processes are needed.

```sh
$ lein sass4clj auto
```

To generate the .css files from the .scss sources. And:

```
$ lein ring server
```

Your app should now be running on [localhost:3000](http://localhost:5000/).
