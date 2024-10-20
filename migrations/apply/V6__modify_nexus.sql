ALTER TABLE host_nexus DROP CONSTRAINT fk_host_nexus_host_id__id;
ALTER TABLE host_nexus ADD CONSTRAINT fk_host_nexus_host_id__id FOREIGN KEY (host_id) REFERENCES host(id) ON DELETE CASCADE ON UPDATE RESTRICT;
ALTER TABLE host_nexus DROP CONSTRAINT fk_host_nexus_nexus_id__id;
ALTER TABLE host_nexus ADD CONSTRAINT fk_host_nexus_nexus_id__id FOREIGN KEY (nexus_id) REFERENCES nexus(id) ON DELETE CASCADE ON UPDATE RESTRICT;
