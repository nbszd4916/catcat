-- 创建数据库
CREATE DATABASE IF NOT EXISTS cat_dorm DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE cat_dorm;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `nickname` VARCHAR(50) COMMENT '昵称',
    `avatar` VARCHAR(255) COMMENT '头像',
    `is_admin` INT DEFAULT 0 COMMENT '是否管理员: 0=普通用户, 1=管理员',
    `is_approved` INT DEFAULT 1 COMMENT '审核状态: 0=待审核, 1=已通过, 2=已拒绝'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 店铺表
CREATE TABLE IF NOT EXISTS `store` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL COMMENT '店铺名称',
    `address` VARCHAR(255) NOT NULL COMMENT '店铺地址',
    `phone` VARCHAR(20) COMMENT '联系电话',
    `description` TEXT COMMENT '店铺介绍',
    `images` TEXT COMMENT '店铺图片(逗号分隔)'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='店铺表';

-- 订单表
CREATE TABLE IF NOT EXISTS `order` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `contact` VARCHAR(100) COMMENT '联系方式',
    `pick_address` VARCHAR(255) COMMENT '取猫地址',
    `start_time` DATETIME COMMENT '寄养开始时间',
    `duration` INT COMMENT '寄养时长(天)',
    `need_pickup` INT DEFAULT 0 COMMENT '是否需要接送服务: 0=否, 1=需要送猫, 2=需要取猫, 3=都需要',
    `send_address` VARCHAR(255) COMMENT '送猫地址',
    `total_deposit` DECIMAL(10, 2) COMMENT '定金总额',
    `status` VARCHAR(20) DEFAULT 'unpaid' COMMENT '订单状态: unpaid=未付款, processing=进行中, completed=已完成, refunded=已退款',
    `process_substatus` VARCHAR(50) COMMENT '进行中子状态: boarding=寄养中, awaiting_pickup=待取猫',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 猫咪表
CREATE TABLE IF NOT EXISTS `cat` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `order_id` BIGINT COMMENT '所属订单ID',
    `cat_name` VARCHAR(50) NOT NULL COMMENT '猫咪名字',
    `breed` VARCHAR(50) COMMENT '品种',
    `weight` DECIMAL(5, 2) COMMENT '体重(kg)',
    `age` INT COMMENT '年龄(月龄)',
    `image` VARCHAR(255) COMMENT '图片路径',
    `remarks` TEXT COMMENT '备注',
    `mi_code` VARCHAR(50) UNIQUE COMMENT '咪码',
    `store_id` BIGINT COMMENT '所在店铺ID',
    `staff_id` BIGINT COMMENT '负责员工ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='猫咪表';

-- 插入测试数据
-- 插入管理员账号 (用户名: admin, 密码: admin123)
INSERT INTO `user` (`username`, `password`, `nickname`, `is_admin`, `is_approved`) 
VALUES ('admin', 'admin123', '超级管理员', 1, 1);

-- 插入普通用户 (用户名: user, 密码: user123)
INSERT INTO `user` (`username`, `password`, `nickname`, `is_admin`, `is_approved`) 
VALUES ('user', 'user123', '测试用户', 0, 1);

-- 插入店铺信息
INSERT INTO `store` (`name`, `address`, `phone`, `description`) VALUES
('猫宿舍·朝阳店', '北京市朝阳区三里屯SOHO 3号楼201', '010-88888888', '位于繁华商圈,交通便利,24小时实时监控,独立猫房设计'),
('猫宿舍·海淀店', '北京市海淀区中关村大街1号科技大厦5层', '010-66666666', '科技园区旗舰店,智能化管理系统,专业兽医团队'),
('猫宿舍·西城店', '北京市西城区金融街购物中心B1层', '010-99999999', '金融街核心区域,高端服务品质,提供定制营养餐');

-- 插入测试订单
INSERT INTO `order` (`user_id`, `contact`, `pick_address`, `start_time`, `duration`, `need_pickup`, `send_address`, `total_deposit`, `status`, `create_time`) VALUES
(2, '13800138000', '北京市朝阳区某某小区1号楼101', '2025-01-05 10:00:00', 7, 1, '北京市朝阳区某某小区1号楼101', 175.00, 'processing', '2025-01-01 14:30:00'),
(2, '13800138001', '北京市海淀区某某路88号', '2025-01-10 09:00:00', 14, 0, NULL, 350.00, 'unpaid', '2025-01-02 10:20:00');

-- 插入测试猫咪（订单1的猫咪 - 正在寄养中）
INSERT INTO `cat` (`order_id`, `cat_name`, `breed`, `weight`, `age`, `remarks`, `mi_code`, `store_id`) VALUES
(1, '小橘', '橘猫', 4.50, 18, '性格温顺,喜欢晒太阳', 'CAT20250001', 1),
(1, '咪咪', '英短', 3.80, 12, '有点害羞,需要多陪伴', 'CAT20250002', 1);

-- 插入测试猫咪（订单2的猫咪 - 待付款）
INSERT INTO `cat` (`order_id`, `cat_name`, `breed`, `weight`, `age`, `remarks`, `mi_code`) VALUES
(2, '布丁', '布偶', 5.20, 24, '很乖,爱吃小鱼干', 'CAT20250003');
