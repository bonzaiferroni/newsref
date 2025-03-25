CREATE TABLE IF NOT EXISTS huddle_comment (id BIGSERIAL PRIMARY KEY, huddle_id BIGINT NOT NULL, comment_id BIGINT NOT NULL);
CREATE INDEX huddle_comment_huddle_id ON huddle_comment (huddle_id);
CREATE INDEX huddle_comment_comment_id ON huddle_comment (comment_id);
ALTER TABLE huddle_comment ADD CONSTRAINT fk_huddle_comment_huddle_id__id FOREIGN KEY (huddle_id) REFERENCES huddle(id) ON DELETE CASCADE ON UPDATE RESTRICT;
ALTER TABLE huddle_comment ADD CONSTRAINT fk_huddle_comment_comment_id__id FOREIGN KEY (comment_id) REFERENCES "comment"(id) ON DELETE CASCADE ON UPDATE RESTRICT;
CREATE SEQUENCE IF NOT EXISTS huddle_comment_id_seq START WITH 1 MINVALUE 1 MAXVALUE 9223372036854775807;
ALTER TABLE huddle_response ADD PRIMARY KEY (id);
