CREATE TABLE IF NOT EXISTS "user" (id BIGSERIAL PRIMARY KEY, "name" TEXT NULL, username TEXT NOT NULL, hashed_password TEXT NOT NULL, salt TEXT NOT NULL, email TEXT NULL, roles TEXT[] NOT NULL, avatar_url TEXT NULL, venmo TEXT NULL, created_at TIMESTAMP NOT NULL, updated_at TIMESTAMP NOT NULL);
CREATE TABLE IF NOT EXISTS sessiontoken (id BIGSERIAL PRIMARY KEY, user_id BIGINT NOT NULL, token TEXT NOT NULL, created_at BIGINT NOT NULL, expires_at BIGINT NOT NULL, issuer TEXT NOT NULL, CONSTRAINT fk_sessiontoken_user_id__id FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE ON UPDATE RESTRICT);
CREATE TABLE IF NOT EXISTS nexus (id SERIAL PRIMARY KEY, "name" TEXT NOT NULL);
CREATE TABLE IF NOT EXISTS host (id SERIAL PRIMARY KEY, nexus_id INT NULL, core TEXT NOT NULL, "name" TEXT NULL, logo TEXT NULL, robots_txt TEXT NULL, is_redirect BOOLEAN NULL, disallowed TEXT[] NOT NULL, domains TEXT[] NOT NULL, junk_params TEXT[] NOT NULL, nav_params TEXT[] NOT NULL, CONSTRAINT fk_host_nexus_id__id FOREIGN KEY (nexus_id) REFERENCES nexus(id) ON DELETE SET NULL ON UPDATE RESTRICT);
CREATE INDEX host_nexus_id ON host (nexus_id);
ALTER TABLE host ADD CONSTRAINT host_core_unique UNIQUE (core);
CREATE TABLE IF NOT EXISTS note (id BIGSERIAL PRIMARY KEY, user_id BIGINT NOT NULL, subject TEXT NOT NULL, body TEXT NOT NULL, created_at TIMESTAMP NOT NULL, modified_at TIMESTAMP NOT NULL, CONSTRAINT fk_note_user_id__id FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE SET NULL ON UPDATE RESTRICT);
CREATE INDEX note_user_id ON note (user_id);
CREATE INDEX note_created_at ON note (created_at);
CREATE INDEX note_modified_at ON note (modified_at);
CREATE TABLE IF NOT EXISTS "source" (id BIGSERIAL PRIMARY KEY, host_id INT NOT NULL, note_id BIGINT NULL, url TEXT NOT NULL, title TEXT NULL, score INT NULL, source_type INT NULL, image_url TEXT NULL, thumbnail TEXT NULL, embed TEXT NULL, content_count INT NULL, embedding vector(1536) NULL, seen_at TIMESTAMP NOT NULL, accessed_at TIMESTAMP NULL, published_at TIMESTAMP NULL, CONSTRAINT fk_source_host_id__id FOREIGN KEY (host_id) REFERENCES host(id) ON DELETE CASCADE ON UPDATE RESTRICT, CONSTRAINT fk_source_note_id__id FOREIGN KEY (note_id) REFERENCES note(id) ON DELETE SET NULL ON UPDATE RESTRICT);
CREATE INDEX source_host_id ON "source" (host_id);
CREATE INDEX source_note_id ON "source" (note_id);
ALTER TABLE "source" ADD CONSTRAINT source_url_unique UNIQUE (url);
CREATE INDEX source_seen_at ON "source" (seen_at);
CREATE INDEX source_published_at ON "source" (published_at);
CREATE TABLE IF NOT EXISTS lead (id BIGSERIAL PRIMARY KEY, url TEXT NOT NULL, host_id INT NOT NULL, source_id BIGINT NULL, CONSTRAINT fk_lead_host_id__id FOREIGN KEY (host_id) REFERENCES host(id) ON DELETE CASCADE ON UPDATE RESTRICT, CONSTRAINT fk_lead_source_id__id FOREIGN KEY (source_id) REFERENCES "source"(id) ON DELETE SET NULL ON UPDATE RESTRICT);
ALTER TABLE lead ADD CONSTRAINT lead_url_unique UNIQUE (url);
CREATE INDEX lead_host_id ON lead (host_id);
CREATE INDEX lead_source_id ON lead (source_id);
CREATE TABLE IF NOT EXISTS "content" (id BIGSERIAL PRIMARY KEY, "text" TEXT NOT NULL);
ALTER TABLE "content" ADD CONSTRAINT content_text_unique UNIQUE ("text");
CREATE TABLE IF NOT EXISTS link (id BIGSERIAL PRIMARY KEY, source_id BIGINT NOT NULL, lead_id BIGINT NULL, content_id BIGINT NULL, url TEXT NOT NULL, url_text TEXT NOT NULL, is_external BOOLEAN NOT NULL, CONSTRAINT fk_link_source_id__id FOREIGN KEY (source_id) REFERENCES "source"(id) ON DELETE CASCADE ON UPDATE RESTRICT, CONSTRAINT fk_link_lead_id__id FOREIGN KEY (lead_id) REFERENCES lead(id) ON DELETE SET NULL ON UPDATE RESTRICT, CONSTRAINT fk_link_content_id__id FOREIGN KEY (content_id) REFERENCES "content"(id) ON DELETE SET NULL ON UPDATE RESTRICT);
CREATE INDEX link_source_id ON link (source_id);
CREATE INDEX link_lead_id ON link (lead_id);
CREATE INDEX link_content_id ON link (content_id);
CREATE TABLE IF NOT EXISTS source_score (id BIGSERIAL PRIMARY KEY, source_id BIGINT NOT NULL, link_id BIGINT NOT NULL, score INT NOT NULL, scored_at TIMESTAMP NOT NULL, CONSTRAINT fk_source_score_source_id__id FOREIGN KEY (source_id) REFERENCES "source"(id) ON DELETE CASCADE ON UPDATE RESTRICT, CONSTRAINT fk_source_score_link_id__id FOREIGN KEY (link_id) REFERENCES link(id) ON DELETE CASCADE ON UPDATE RESTRICT);
CREATE TABLE IF NOT EXISTS article (id BIGSERIAL PRIMARY KEY, source_id BIGINT NOT NULL, headline TEXT NOT NULL, alternative_headline TEXT NULL, description TEXT NULL, cannon_url TEXT NULL, "section" TEXT NULL, keywords TEXT[] NULL, word_count INT NULL, is_free BOOLEAN NULL, "language" TEXT NULL, comment_count INT NULL, modified_at TIMESTAMP NULL, CONSTRAINT fk_article_source_id__id FOREIGN KEY (source_id) REFERENCES "source"(id) ON DELETE CASCADE ON UPDATE RESTRICT);
CREATE INDEX article_source_id ON article (source_id);
CREATE TABLE IF NOT EXISTS feed (id SERIAL PRIMARY KEY, url TEXT NOT NULL, selector TEXT NOT NULL, "external" BOOLEAN DEFAULT FALSE NOT NULL, created_at TIMESTAMP DEFAULT (NOW()) NOT NULL);
CREATE TABLE IF NOT EXISTS lead_job (id BIGSERIAL PRIMARY KEY, feed_id INT NULL, lead_id BIGINT NOT NULL, headline TEXT NULL, is_external BOOLEAN NOT NULL, fresh_at TIMESTAMP NULL, CONSTRAINT fk_lead_job_feed_id__id FOREIGN KEY (feed_id) REFERENCES feed(id) ON DELETE SET NULL ON UPDATE RESTRICT, CONSTRAINT fk_lead_job_lead_id__id FOREIGN KEY (lead_id) REFERENCES lead(id) ON DELETE CASCADE ON UPDATE RESTRICT);
CREATE INDEX lead_job_lead_id ON lead_job (lead_id);
CREATE INDEX lead_job_fresh_at ON lead_job (fresh_at);
CREATE TABLE IF NOT EXISTS lead_result (id BIGSERIAL PRIMARY KEY, lead_id BIGINT NOT NULL, "result" INT NOT NULL, attempted_at TIMESTAMP NOT NULL, strategy INT NULL, CONSTRAINT fk_lead_result_lead_id__id FOREIGN KEY (lead_id) REFERENCES lead(id) ON DELETE SET NULL ON UPDATE RESTRICT);
CREATE TABLE IF NOT EXISTS source_content (id BIGSERIAL PRIMARY KEY, source_id BIGINT NOT NULL, content_id BIGINT NOT NULL, CONSTRAINT fk_source_content_source_id__id FOREIGN KEY (source_id) REFERENCES "source"(id) ON DELETE CASCADE ON UPDATE RESTRICT, CONSTRAINT fk_source_content_content_id__id FOREIGN KEY (content_id) REFERENCES "content"(id) ON DELETE CASCADE ON UPDATE RESTRICT);
CREATE TABLE IF NOT EXISTS author (id SERIAL PRIMARY KEY, "name" TEXT NOT NULL, url TEXT NULL, bylines TEXT[] NOT NULL);
CREATE INDEX author_name ON author ("name");
CREATE INDEX author_bylines ON author (bylines);
CREATE TABLE IF NOT EXISTS host_author (host_id INT, author_id INT, CONSTRAINT pk_host_author PRIMARY KEY (host_id, author_id), CONSTRAINT fk_host_author_host_id__id FOREIGN KEY (host_id) REFERENCES host(id) ON DELETE CASCADE ON UPDATE RESTRICT, CONSTRAINT fk_host_author_author_id__id FOREIGN KEY (author_id) REFERENCES author(id) ON DELETE CASCADE ON UPDATE RESTRICT);
CREATE INDEX host_author_host_id ON host_author (host_id);
CREATE INDEX host_author_author_id ON host_author (author_id);
CREATE TABLE IF NOT EXISTS source_author (source_id BIGINT, author_id INT, CONSTRAINT pk_source_author PRIMARY KEY (source_id, author_id), CONSTRAINT fk_source_author_source_id__id FOREIGN KEY (source_id) REFERENCES "source"(id) ON DELETE CASCADE ON UPDATE RESTRICT, CONSTRAINT fk_source_author_author_id__id FOREIGN KEY (author_id) REFERENCES author(id) ON DELETE CASCADE ON UPDATE RESTRICT);
CREATE INDEX source_author_source_id ON source_author (source_id);
CREATE INDEX source_author_author_id ON source_author (author_id);
CREATE TABLE IF NOT EXISTS scoop (id BIGSERIAL PRIMARY KEY, uri TEXT NOT NULL, html TEXT NOT NULL);
CREATE TABLE IF NOT EXISTS feed_source (id SERIAL PRIMARY KEY, source_id BIGINT NOT NULL, score INT NOT NULL, created_at TIMESTAMP NOT NULL, "source" JSON NOT NULL, CONSTRAINT fk_feed_source_source_id__id FOREIGN KEY (source_id) REFERENCES "source"(id) ON DELETE CASCADE ON UPDATE RESTRICT);
CREATE TABLE IF NOT EXISTS source_note (id BIGSERIAL PRIMARY KEY, source_id BIGINT NOT NULL, note_id BIGINT NOT NULL, CONSTRAINT fk_source_note_source_id__id FOREIGN KEY (source_id) REFERENCES "source"(id) ON DELETE CASCADE ON UPDATE RESTRICT, CONSTRAINT fk_source_note_note_id__id FOREIGN KEY (note_id) REFERENCES note(id) ON DELETE SET NULL ON UPDATE RESTRICT);
CREATE INDEX source_note_source_id ON source_note (source_id);
CREATE INDEX source_note_note_id ON source_note (note_id);
CREATE SEQUENCE IF NOT EXISTS user_id_seq START WITH 1 MINVALUE 1 MAXVALUE 9223372036854775807;
CREATE SEQUENCE IF NOT EXISTS SessionToken_id_seq START WITH 1 MINVALUE 1 MAXVALUE 9223372036854775807;
CREATE SEQUENCE IF NOT EXISTS source_id_seq START WITH 1 MINVALUE 1 MAXVALUE 9223372036854775807;
CREATE SEQUENCE IF NOT EXISTS source_score_id_seq START WITH 1 MINVALUE 1 MAXVALUE 9223372036854775807;
CREATE SEQUENCE IF NOT EXISTS link_id_seq START WITH 1 MINVALUE 1 MAXVALUE 9223372036854775807;
CREATE SEQUENCE IF NOT EXISTS host_id_seq START WITH 1 MINVALUE 1 MAXVALUE 9223372036854775807;
CREATE SEQUENCE IF NOT EXISTS article_id_seq START WITH 1 MINVALUE 1 MAXVALUE 9223372036854775807;
CREATE SEQUENCE IF NOT EXISTS lead_id_seq START WITH 1 MINVALUE 1 MAXVALUE 9223372036854775807;
CREATE SEQUENCE IF NOT EXISTS lead_job_id_seq START WITH 1 MINVALUE 1 MAXVALUE 9223372036854775807;
CREATE SEQUENCE IF NOT EXISTS lead_result_id_seq START WITH 1 MINVALUE 1 MAXVALUE 9223372036854775807;
CREATE SEQUENCE IF NOT EXISTS source_content_id_seq START WITH 1 MINVALUE 1 MAXVALUE 9223372036854775807;
CREATE SEQUENCE IF NOT EXISTS content_id_seq START WITH 1 MINVALUE 1 MAXVALUE 9223372036854775807;
CREATE SEQUENCE IF NOT EXISTS author_id_seq START WITH 1 MINVALUE 1 MAXVALUE 9223372036854775807;
CREATE SEQUENCE IF NOT EXISTS scoop_id_seq START WITH 1 MINVALUE 1 MAXVALUE 9223372036854775807;
CREATE SEQUENCE IF NOT EXISTS feed_id_seq START WITH 1 MINVALUE 1 MAXVALUE 9223372036854775807;
CREATE SEQUENCE IF NOT EXISTS nexus_id_seq START WITH 1 MINVALUE 1 MAXVALUE 9223372036854775807;
CREATE SEQUENCE IF NOT EXISTS feed_source_id_seq START WITH 1 MINVALUE 1 MAXVALUE 9223372036854775807;
CREATE SEQUENCE IF NOT EXISTS note_id_seq START WITH 1 MINVALUE 1 MAXVALUE 9223372036854775807;
CREATE SEQUENCE IF NOT EXISTS source_note_id_seq START WITH 1 MINVALUE 1 MAXVALUE 9223372036854775807;
