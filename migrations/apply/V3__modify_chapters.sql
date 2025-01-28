ALTER TABLE chapter ADD happened_at TIMESTAMP NOT NULL;
CREATE INDEX chapter_happened_at ON chapter (happened_at);
ALTER TABLE chapter DROP COLUMN average;
