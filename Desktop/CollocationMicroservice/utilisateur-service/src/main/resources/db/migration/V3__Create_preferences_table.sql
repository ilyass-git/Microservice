CREATE TABLE IF NOT EXISTS preferences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    budget DECIMAL(12, 2),
    city VARCHAR(255),
    smoking_allowed BOOLEAN,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



