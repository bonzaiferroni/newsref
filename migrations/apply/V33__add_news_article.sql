CREATE TABLE IF NOT EXISTS news_article (page_id BIGINT PRIMARY KEY, summary TEXT NOT NULL, objectivity REAL NOT NULL, is_news_article BOOLEAN NOT NULL, CONSTRAINT fk_news_article_page_id__id FOREIGN KEY (page_id) REFERENCES "source"(id) ON DELETE CASCADE ON UPDATE RESTRICT);
CREATE INDEX news_article_page_id ON news_article (page_id);
