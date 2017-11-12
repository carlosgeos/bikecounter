-- :name add-record :! :n
-- :doc inserts a single entry in the database
INSERT INTO "Flow" (ts, today, parcial)
VALUES (:ts::TIMESTAMP WITH TIME ZONE, :today::INTEGER, :parcial::INTEGER);

-- :name select-last-record :? :one
-- :doc
SELECT * FROM "Flow" ORDER BY id DESC LIMIT 1;

-- :name amend-hourly :! :n
-- :doc updates the last row with the real value of bikers
UPDATE "Flow"
SET thishour = :thishour
WHERE id = :id;
