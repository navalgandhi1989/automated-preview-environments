-- liquibase formatted sql

-- changeset git-username:alter_user_add_date_of_birth_column logicalFilePath:path-independent
ALTER TABLE user ADD COLUMN `date_of_birth` DATETIME NULL;
-- rollback ALTER TABLE user DROP COLUMN date_of_birth;
