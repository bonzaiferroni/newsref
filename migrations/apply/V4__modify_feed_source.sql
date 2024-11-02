ALTER TABLE feed_source ADD score INT NOT NULL;
ALTER TABLE feed_source ADD created_at TIMESTAMP NOT NULL;
ALTER TABLE feed_source DROP COLUMN checked_at;
