ALTER TABLE author ADD url TEXT NULL;
ALTER TABLE author ALTER COLUMN "name" TYPE TEXT, ALTER COLUMN "name" SET NOT NULL;
