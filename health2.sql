/*
 Navicat Premium Data Transfer

 Source Server         : conn-localhost
 Source Server Type    : MySQL
 Source Server Version : 80030
 Source Host           : localhost:3306
 Source Schema         : health2

 Target Server Type    : MySQL
 Target Server Version : 80030
 File Encoding         : 65001

 Date: 22/03/2026 21:33:46
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for health_item
-- ----------------------------
DROP TABLE IF EXISTS `health_item`;
CREATE TABLE `health_item`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '健康项信息表主键ID，自增',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '健康项名',
  `detail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '简介',
  `icon_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '图标',
  `unit` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '单位',
  `symbol` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '符号',
  `normal_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '正常值范围',
  `user_id` int NULL DEFAULT NULL COMMENT '用户ID，外键，关联的是用户表',
  `is_global` tinyint(1) NULL DEFAULT NULL COMMENT '是否是公有健康项',
  `create_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '健康项信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for health_record
-- ----------------------------
DROP TABLE IF EXISTS `health_record`;
CREATE TABLE `health_record`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户健康记录表主键ID，自增',
  `user_id` int NULL DEFAULT NULL COMMENT '用户ID，外键，关联的是用户表',
  `health_item_id` int UNSIGNED NULL DEFAULT NULL COMMENT '健康项ID，外键，关联的是健康项信息表',
  `value` double NULL DEFAULT NULL COMMENT '记录的值',
  `create_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '记录时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_health_record_health_item`(`health_item_id` ASC) USING BTREE,
  CONSTRAINT `fk_health_record_health_item` FOREIGN KEY (`health_item_id`) REFERENCES `health_item` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 65 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '健康记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for persistent_chat
-- ----------------------------
DROP TABLE IF EXISTS `persistent_chat`;
CREATE TABLE `persistent_chat`  (
  `id` int NOT NULL COMMENT '会话id，主键',
  `jsonContent` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '聊天记忆',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户编号',
  `account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户账号',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户昵称',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户密码',
  `avatar` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '用户头像',
  `role` int NULL DEFAULT NULL COMMENT '用户角色',
  `create_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户注册时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
