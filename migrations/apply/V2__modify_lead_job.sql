ALTER TABLE lead_job DROP CONSTRAINT fk_lead_job_feed_id__id;
ALTER TABLE lead_job ADD CONSTRAINT fk_lead_job_feed_id__id FOREIGN KEY (feed_id) REFERENCES feed(id) ON DELETE SET NULL ON UPDATE RESTRICT;
