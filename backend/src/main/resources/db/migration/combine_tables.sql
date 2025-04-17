-- 合并所有表的SQL脚本
-- 作者：t
-- 日期：2025-04-18
-- 功能：合并所有表定义，并修复optimize_search.sql中的问题

-- ------------- 笔记评论表 (V20240320_1) -------------
CREATE TABLE IF NOT EXISTS note_comment (
    id INT PRIMARY KEY AUTO_INCREMENT,
    note_id INT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (note_id) REFERENCES note(id),
    FOREIGN KEY (user_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------- 评论和点赞表 (V20240325_1) -------------
-- 评论表
CREATE TABLE IF NOT EXISTS `comment` (
  `comment_id` INT NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `note_id` INT UNSIGNED NOT NULL COMMENT '笔记ID',
  `author_id` BIGINT UNSIGNED NOT NULL COMMENT '作者ID',
  `parent_id` INT DEFAULT NULL COMMENT '父评论ID',
  `content` TEXT NOT NULL COMMENT '评论内容',
  `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
  `reply_count` INT NOT NULL DEFAULT 0 COMMENT '回复数',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`comment_id`),
  KEY `idx_note_id` (`note_id`),
  KEY `idx_author_id` (`author_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_created_at` (`created_at`),
  CONSTRAINT `fk_comment_note` FOREIGN KEY (`note_id`) REFERENCES `note` (`note_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_comment_author` FOREIGN KEY (`author_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_comment_parent` FOREIGN KEY (`parent_id`) REFERENCES `comment` (`comment_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- 评论点赞表
CREATE TABLE IF NOT EXISTS `comment_like` (
  `comment_like_id` INT NOT NULL AUTO_INCREMENT COMMENT '评论点赞ID',
  `comment_id` INT NOT NULL COMMENT '评论ID',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`comment_like_id`),
  UNIQUE KEY `uk_comment_user` (`comment_id`, `user_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_comment_like_comment` FOREIGN KEY (`comment_id`) REFERENCES `comment` (`comment_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_comment_like_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论点赞表';

-- ------------- 消息表 (V20240325_2) -------------
-- 消息表
CREATE TABLE IF NOT EXISTS `message` (
  `message_id` INT NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `receiver_id` BIGINT UNSIGNED NOT NULL COMMENT '接收者ID',
  `sender_id` BIGINT UNSIGNED NOT NULL COMMENT '发送者ID',
  `type` VARCHAR(20) NOT NULL COMMENT '消息类型: COMMENT-评论, LIKE-点赞',
  `target_id` INT NOT NULL COMMENT '目标ID(评论ID或笔记ID)',
  `content` TEXT NOT NULL COMMENT '消息内容',
  `is_read` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已读: 0-未读, 1-已读',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`message_id`),
  KEY `idx_receiver_id` (`receiver_id`),
  KEY `idx_sender_id` (`sender_id`),
  KEY `idx_created_at` (`created_at`),
  CONSTRAINT `fk_message_receiver` FOREIGN KEY (`receiver_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_message_sender` FOREIGN KEY (`sender_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息表';

-- ------------- 邮箱验证 (V20240326_1) -------------
-- 用户表添加邮箱相关字段（如果不存在）
ALTER TABLE user ADD COLUMN IF NOT EXISTS email VARCHAR(100) COMMENT '用户邮箱';
ALTER TABLE user ADD COLUMN IF NOT EXISTS email_verified BOOLEAN DEFAULT FALSE COMMENT '邮箱是否验证';
ALTER TABLE user ADD UNIQUE INDEX IF NOT EXISTS idx_email (email);

-- 创建邮箱验证码表（如果不存在）
CREATE TABLE IF NOT EXISTS email_verify_code (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    email VARCHAR(100) NOT NULL COMMENT '邮箱地址',
    code VARCHAR(6) NOT NULL COMMENT '验证码',
    type VARCHAR(20) NOT NULL COMMENT '验证码类型：REGISTER-注册，RESET_PASSWORD-重置密码',
    expired_at TIMESTAMP NOT NULL COMMENT '过期时间',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    used BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否已使用',
    PRIMARY KEY (id),
    INDEX idx_email (email),
    INDEX idx_code (code),
    INDEX idx_expired_at (expired_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='邮箱验证码表';

-- ------------- 搜索优化 (V20240327_1 修复版) -------------
-- 为笔记表添加搜索向量字段和索引（修复：title和content确保是note表中的字段）
ALTER TABLE note 
ADD COLUMN IF NOT EXISTS search_vector TEXT GENERATED ALWAYS AS
  (CONCAT_WS(' ', title, content)) STORED;

-- 添加全文索引（如果不存在）
ALTER TABLE note 
ADD FULLTEXT INDEX IF NOT EXISTS idx_note_search(search_vector);

-- 添加普通索引（如果不存在，修复：user_id改为author_id）
ALTER TABLE note 
ADD INDEX IF NOT EXISTS idx_created_at(created_at);

ALTER TABLE note 
ADD INDEX IF NOT EXISTS idx_author_id(author_id);

-- 为标签表添加索引（如果不存在，假设tag表已存在）
-- 如果tag表不存在，可以先创建tag表
CREATE TABLE IF NOT EXISTS `tag` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `name` VARCHAR(50) NOT NULL COMMENT '标签名称',
  `user_id` BIGINT UNSIGNED COMMENT '用户ID',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_name` (`name`),
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

-- 为用户表添加组合索引（如果不存在）
ALTER TABLE user 
ADD INDEX IF NOT EXISTS idx_search(username, account, email);

-- 笔记标签关联表（如果不存在）
CREATE TABLE IF NOT EXISTS `note_tag` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `note_id` INT NOT NULL COMMENT '笔记ID',
  `tag_id` INT NOT NULL COMMENT '标签ID',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_note_tag` (`note_id`, `tag_id`),
  INDEX `idx_tag_id` (`tag_id`),
  INDEX `idx_note_id` (`note_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='笔记标签关联表';

