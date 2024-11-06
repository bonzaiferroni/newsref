ALTER TABLE lead_job DROP CONSTRAINT fk_lead_job_lead_id__id;
ALTER TABLE lead_job ADD CONSTRAINT fk_lead_job_lead_id__id FOREIGN KEY (lead_id) REFERENCES lead(id) ON DELETE CASCADE ON UPDATE RESTRICT;
ALTER TABLE lead_result DROP CONSTRAINT fk_lead_result_lead_id__id;
ALTER TABLE lead_result ADD CONSTRAINT fk_lead_result_lead_id__id FOREIGN KEY (lead_id) REFERENCES lead(id) ON DELETE SET NULL ON UPDATE RESTRICT;
