--liquibase formatted sql

--changeset jobstream:V20_add_domain_to_companies
ALTER TABLE companies ADD COLUMN domain VARCHAR(255);
