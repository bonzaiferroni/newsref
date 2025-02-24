CREATE TABLE IF NOT EXISTS page_location (page_id BIGINT, location_id INT, CONSTRAINT pk_page_location PRIMARY KEY (page_id, location_id), CONSTRAINT fk_page_location_page_id__id FOREIGN KEY (page_id) REFERENCES "source"(id) ON DELETE CASCADE ON UPDATE RESTRICT, CONSTRAINT fk_page_location_location_id__id FOREIGN KEY (location_id) REFERENCES "location"(id) ON DELETE CASCADE ON UPDATE RESTRICT);
CREATE INDEX page_location_page_id ON page_location (page_id);
CREATE INDEX page_location_location_id ON page_location (location_id);
ALTER TABLE "location" ADD geo_point POINT NOT NULL;
ALTER TABLE "location" ADD north_east POINT NOT NULL;
ALTER TABLE "location" ADD south_west POINT NOT NULL;
