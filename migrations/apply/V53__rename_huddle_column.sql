CREATE SEQUENCE IF NOT EXISTS huddle_response_id_seq START WITH 1 MINVALUE 1 MAXVALUE 9223372036854775807;
ALTER TABLE huddle ADD target_id BIGINT NULL;
CREATE INDEX huddle_target_id ON huddle (target_id);
ALTER TABLE huddle_response ALTER COLUMN id SET DEFAULT nextval('huddle_response_id_seq');
ALTER SEQUENCE huddle_response_id_seq OWNED BY huddle_response.id;
ALTER TABLE huddle ADD CONSTRAINT fk_huddle_target_id__id FOREIGN KEY (target_id) REFERENCES huddle(id) ON DELETE CASCADE ON UPDATE RESTRICT;
