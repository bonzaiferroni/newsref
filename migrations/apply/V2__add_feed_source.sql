ALTER TABLE feed_source ADD authors TEXT[] NULL;
ALTER TABLE feed_source ALTER COLUMN headline TYPE TEXT, ALTER COLUMN headline DROP NOT NULL;
ALTER TABLE feed_source ALTER COLUMN word_count TYPE INT, ALTER COLUMN word_count DROP NOT NULL;
ALTER TABLE feed_source ALTER COLUMN published_at TYPE TIMESTAMP, ALTER COLUMN published_at DROP NOT NULL;
