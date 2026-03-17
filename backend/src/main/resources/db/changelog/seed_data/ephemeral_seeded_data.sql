-- liquibase formatted sql
-- changeset git-username:ephemeral_dataset_v3 runOnChange:True; logicalFilePath:path-independent
SET FOREIGN_KEY_CHECKS = 0; 
TRUNCATE table user;
INSERT INTO `user`(`id`,`first_name`,`last_name`) VALUES (1,'Admin','User');
INSERT INTO `user`(`id`,`first_name`,`last_name`) VALUES (2,'John','Smith');
INSERT INTO `user`(`id`,`first_name`,`last_name`) VALUES (3,'Emily','Johnson');
INSERT INTO `user`(`id`,`first_name`,`last_name`) VALUES (4,'Michael','Brown');
INSERT INTO `user`(`id`,`first_name`,`last_name`) VALUES (5,'Sarah','Davis');
INSERT INTO `user`(`id`,`first_name`,`last_name`) VALUES (6,'David','Wilson');
INSERT INTO `user`(`id`,`first_name`,`last_name`) VALUES (7,'Jessica','Taylor');
INSERT INTO `user`(`id`,`first_name`,`last_name`) VALUES (8,'Daniel','Anderson');
INSERT INTO `user`(`id`,`first_name`,`last_name`) VALUES (9,'Ashley','Thomas');
INSERT INTO `user`(`id`,`first_name`,`last_name`) VALUES (10,'Matthew','Moore');
INSERT INTO `user`(`id`,`first_name`,`last_name`) VALUES (11,'Olivia','Martin');
INSERT INTO `user`(`id`,`first_name`,`last_name`) VALUES (12,'James','Lee');
INSERT INTO `user`(`id`,`first_name`,`last_name`) VALUES (13,'Sophia','Perez');
INSERT INTO `user`(`id`,`first_name`,`last_name`) VALUES (14,'Benjamin','Clark');
INSERT INTO `user`(`id`,`first_name`,`last_name`) VALUES (15,'Isabella','Lewis');
INSERT INTO `user`(`id`,`first_name`,`last_name`) VALUES (16,'William','Walker');
INSERT INTO `user`(`id`,`first_name`,`last_name`) VALUES (17,'Mia','Hall');
INSERT INTO `user`(`id`,`first_name`,`last_name`) VALUES (18,'Ethan','Allen');
INSERT INTO `user`(`id`,`first_name`,`last_name`) VALUES (19,'Charlotte','Young');
INSERT INTO `user`(`id`,`first_name`,`last_name`) VALUES (20,'Alexander','King');
SET FOREIGN_KEY_CHECKS = 1;