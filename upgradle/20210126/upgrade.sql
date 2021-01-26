-- 修改名字长度 原来20太短了
ALTER TABLE  `dp_charge_config`
MODIFY COLUMN `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称' AFTER `id`;
