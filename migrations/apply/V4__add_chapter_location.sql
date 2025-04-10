ALTER TABLE chapter ADD location_id INT NULL;
ALTER TABLE chapter ADD CONSTRAINT fk_chapter_location_id__id FOREIGN KEY (location_id) REFERENCES "location"(id) ON DELETE SET NULL ON UPDATE RESTRICT;