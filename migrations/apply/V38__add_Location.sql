CREATE TABLE IF NOT EXISTS "location" (id SERIAL PRIMARY KEY, "name" TEXT NOT NULL);
ALTER TABLE "location" ADD CONSTRAINT location_name_unique UNIQUE ("name");
CREATE SEQUENCE IF NOT EXISTS location_id_seq START WITH 1 MINVALUE 1 MAXVALUE 9223372036854775807;
ALTER TABLE news_article ADD location_id INT NULL;
CREATE INDEX news_article_location_id ON news_article (location_id);
ALTER TABLE news_article ADD CONSTRAINT fk_news_article_location_id__id FOREIGN KEY (location_id) REFERENCES "location"(id) ON DELETE CASCADE ON UPDATE RESTRICT;
