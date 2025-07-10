-- 创建评论表
CREATE TABLE IF NOT EXISTS knowledge_comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评论ID',
    kp_id BIGINT NOT NULL COMMENT '知识点ID',
    user_id BIGINT NOT NULL COMMENT '评论者ID',
    content TEXT NOT NULL COMMENT '评论内容',
    parent_id BIGINT NULL COMMENT '父评论ID（用于回复功能）',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_kp_id (kp_id),
    INDEX idx_user_id (user_id),
    INDEX idx_parent_id (parent_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识点评论表';

-- 创建用户点赞表
CREATE TABLE IF NOT EXISTS knowledge_user_likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    kp_id BIGINT NOT NULL COMMENT '知识点ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_kp_user (kp_id, user_id) COMMENT '知识点和用户的唯一约束',
    INDEX idx_kp_id (kp_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户点赞表';

-- 创建用户收藏表
CREATE TABLE IF NOT EXISTS knowledge_user_favorites (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    kp_id BIGINT NOT NULL COMMENT '知识点ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_kp_user (kp_id, user_id) COMMENT '知识点和用户的唯一约束',
    INDEX idx_kp_id (kp_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';

-- 创建用户浏览历史表
CREATE TABLE IF NOT EXISTS knowledge_user_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    kp_id BIGINT NOT NULL COMMENT '知识点ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    viewed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
    duration INT DEFAULT 0 COMMENT '停留时长（秒）',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_kp_id (kp_id),
    INDEX idx_user_id (user_id),
    INDEX idx_viewed_at (viewed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户浏览历史表'; 