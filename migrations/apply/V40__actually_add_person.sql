CREATE TABLE IF NOT EXISTS person (id SERIAL PRIMARY KEY, "name" TEXT NOT NULL, identifier TEXT NOT NULL);
ALTER TABLE person ADD CONSTRAINT person_name_identifier_unique UNIQUE ("name", identifier);
CREATE TABLE IF NOT EXISTS page_peron (page_id BIGINT, person_id INT, CONSTRAINT pk_page_peron PRIMARY KEY (page_id, person_id), CONSTRAINT fk_page_peron_page_id__id FOREIGN KEY (page_id) REFERENCES "source"(id) ON DELETE CASCADE ON UPDATE RESTRICT, CONSTRAINT fk_page_peron_person_id__id FOREIGN KEY (person_id) REFERENCES person(id) ON DELETE CASCADE ON UPDATE RESTRICT);
CREATE INDEX page_peron_page_id ON page_peron (page_id);
CREATE INDEX page_peron_person_id ON page_peron (person_id);
CREATE SEQUENCE IF NOT EXISTS person_id_seq START WITH 1 MINVALUE 1 MAXVALUE 9223372036854775807;
