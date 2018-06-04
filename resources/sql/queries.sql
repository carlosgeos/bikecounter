/* minidoc

First parameter
:query or :? = query with a result-set (default)
:execute or :! = any statement
:returning-execute or :<! = support for INSERT ... RETURNING
:insert or :i! = support for insert and jdbc .getGeneratedKeys

Second parameter
:one or :1 = one row as a hash-map
:many or :* = many rows as a vector of hash-maps
:affected or :n = number of rows affected (inserted/updated/deleted)
:raw = passthrough an untouched result (default)

----------- QUERIES ----------- */

-- :name add-record :! :n
-- :doc inserts a single entry in the database
INSERT INTO "Flow" (ts, today, parcial)
VALUES (:ts::TIMESTAMP WITH TIME ZONE, :today::INTEGER, :parcial::INTEGER);

-- :name get-record :? :one
-- :doc gets a single record with an id
SELECT * FROM "Flow" WHERE id = :id;

-- :name select-last-record :? :one
-- :doc
SELECT * FROM "Flow" ORDER BY id DESC LIMIT 1;

-- :name get-null-or-negative-records :? :*
-- :doc
SELECT * FROM "Flow" WHERE thishour <= 0 ORDER BY id ASC;

-- :name amend-hourly :! :n
-- :doc updates the last row with the real value of bikers
UPDATE "Flow"
SET thishour = :thishour
WHERE id = :id;

-- :name update-record :! :n
-- :doc updates a single record with new info
UPDATE "Flow"
SET thishour = :thishour, parcial = :parcial, today = :today
WHERE id = :id;

-- :name twentyfour-hours :? :*
-- :doc selects the records for the last 24 hours.
SELECT * FROM "Flow"
WHERE ts > :ts AND thishour IS NOT NULL
ORDER BY id ASC;

-- :name get-bikes-from-to :? :*
-- :doc gets all the records in a certain time period
SELECT * FROM "Flow"
WHERE ts BETWEEN :start_time AND :end_time
      AND thishour IS NOT NULL
ORDER BY ts ASC;

-- :name all-records :? :*
-- :doc everything in the db !
SELECT * FROM "Flow";
