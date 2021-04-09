drop table if exists `test`;
create table `test` (
  `id` bigint unsigned not null AUTO_INCREMENT comment '主键',
  `name` varchar(225) not null DEFAULT '' comment '名称',
  `password` varchar(225) not null comment '密码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='测试';
select * from `test`;

insert into `test` (id, name, password) values (1, '测试', 'password');