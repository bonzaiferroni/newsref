CREATE TABLE IF NOT EXISTS page_person (page_id BIGINT, person_id INT, CONSTRAINT pk_page_person PRIMARY KEY (page_id, person_id), CONSTRAINT fk_page_person_page_id__id FOREIGN KEY (page_id) REFERENCES "source"(id) ON DELETE CASCADE ON UPDATE RESTRICT, CONSTRAINT fk_page_person_person_id__id FOREIGN KEY (person_id) REFERENCES person(id) ON DELETE CASCADE ON UPDATE RESTRICT);
CREATE INDEX page_person_page_id ON page_person (page_id);
CREATE INDEX page_person_person_id ON page_person (person_id);
