ALTER TABLE "source" DROP CONSTRAINT fk_source_host_id__id;
ALTER TABLE "source" ADD CONSTRAINT fk_source_host_id__id FOREIGN KEY (host_id) REFERENCES host(id) ON DELETE CASCADE ON UPDATE RESTRICT;
ALTER TABLE source_score DROP CONSTRAINT fk_source_score_source_id__id;
ALTER TABLE source_score ADD CONSTRAINT fk_source_score_source_id__id FOREIGN KEY (source_id) REFERENCES "source"(id) ON DELETE CASCADE ON UPDATE RESTRICT;
ALTER TABLE link DROP CONSTRAINT fk_link_source_id__id;
ALTER TABLE link ADD CONSTRAINT fk_link_source_id__id FOREIGN KEY (source_id) REFERENCES "source"(id) ON DELETE CASCADE ON UPDATE RESTRICT;
ALTER TABLE link DROP CONSTRAINT fk_link_lead_id__id;
ALTER TABLE link ADD CONSTRAINT fk_link_lead_id__id FOREIGN KEY (lead_id) REFERENCES lead(id) ON DELETE SET NULL ON UPDATE RESTRICT;
ALTER TABLE link DROP CONSTRAINT fk_link_content_id__id;
ALTER TABLE link ADD CONSTRAINT fk_link_content_id__id FOREIGN KEY (content_id) REFERENCES "content"(id) ON DELETE SET NULL ON UPDATE RESTRICT;
ALTER TABLE article DROP CONSTRAINT fk_article_source_id__id;
ALTER TABLE article ADD CONSTRAINT fk_article_source_id__id FOREIGN KEY (source_id) REFERENCES "source"(id) ON DELETE CASCADE ON UPDATE RESTRICT;
ALTER TABLE lead DROP CONSTRAINT fk_lead_host_id__id;
ALTER TABLE lead ADD CONSTRAINT fk_lead_host_id__id FOREIGN KEY (host_id) REFERENCES host(id) ON DELETE CASCADE ON UPDATE RESTRICT;
ALTER TABLE lead DROP CONSTRAINT fk_lead_source_id__id;
ALTER TABLE lead ADD CONSTRAINT fk_lead_source_id__id FOREIGN KEY (source_id) REFERENCES "source"(id) ON DELETE SET NULL ON UPDATE RESTRICT;
ALTER TABLE source_content DROP CONSTRAINT fk_source_content_source_id__id;
ALTER TABLE source_content ADD CONSTRAINT fk_source_content_source_id__id FOREIGN KEY (source_id) REFERENCES "source"(id) ON DELETE CASCADE ON UPDATE RESTRICT;
ALTER TABLE source_content DROP CONSTRAINT fk_source_content_content_id__id;
ALTER TABLE source_content ADD CONSTRAINT fk_source_content_content_id__id FOREIGN KEY (content_id) REFERENCES "content"(id) ON DELETE CASCADE ON UPDATE RESTRICT;
ALTER TABLE host_author DROP CONSTRAINT fk_host_author_host_id__id;
ALTER TABLE host_author ADD CONSTRAINT fk_host_author_host_id__id FOREIGN KEY (host_id) REFERENCES host(id) ON DELETE CASCADE ON UPDATE RESTRICT;
ALTER TABLE host_author DROP CONSTRAINT fk_host_author_author_id__id;
ALTER TABLE host_author ADD CONSTRAINT fk_host_author_author_id__id FOREIGN KEY (author_id) REFERENCES author(id) ON DELETE CASCADE ON UPDATE RESTRICT;
ALTER TABLE source_author DROP CONSTRAINT fk_source_author_source_id__id;
ALTER TABLE source_author ADD CONSTRAINT fk_source_author_source_id__id FOREIGN KEY (source_id) REFERENCES "source"(id) ON DELETE CASCADE ON UPDATE RESTRICT;
ALTER TABLE source_author DROP CONSTRAINT fk_source_author_author_id__id;
ALTER TABLE source_author ADD CONSTRAINT fk_source_author_author_id__id FOREIGN KEY (author_id) REFERENCES author(id) ON DELETE CASCADE ON UPDATE RESTRICT;
ALTER TABLE feed_source DROP CONSTRAINT fk_feed_source_source_id__id;
ALTER TABLE feed_source ADD CONSTRAINT fk_feed_source_source_id__id FOREIGN KEY (source_id) REFERENCES "source"(id) ON DELETE CASCADE ON UPDATE RESTRICT;
