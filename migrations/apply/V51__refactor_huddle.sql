CREATE SEQUENCE IF NOT EXISTS huddle_response_id_seq START WITH 1 MINVALUE 1 MAXVALUE 9223372036854775807;
ALTER TABLE news_article ADD article_type_huddle_id BIGINT NULL;
ALTER TABLE huddle_response ADD id BIGSERIAL PRIMARY KEY;
ALTER TABLE news_article ADD CONSTRAINT fk_news_article_article_type_huddle_id__id FOREIGN KEY (article_type_huddle_id) REFERENCES huddle(id) ON DELETE SET NULL ON UPDATE RESTRICT;
