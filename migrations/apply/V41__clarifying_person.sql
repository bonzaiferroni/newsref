ALTER TABLE person ADD identifiers TEXT[] NOT NULL;
ALTER TABLE person DROP COLUMN identifier;
DROP INDEX IF EXISTS person_name_identifier_unique;
