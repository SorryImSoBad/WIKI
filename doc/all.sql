drop table if exists `db_goods`;
create table `db_goods` (
  `id` bigint unsigned not null AUTO_INCREMENT comment '主键',
  `goods_id` int(11) unsigned not null comment '商品id',
  `goods_title` varchar(225) not null DEFAULT '' comment '商品名称',
  `goods_price` decimal(10,2) not null comment '商品价格',
  `create_time` timestamp not null DEFAULT CURRENT_TIMESTAMP comment '创建时间',
  `update_time` timestamp not null DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  comment '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';
select * from db_goods;