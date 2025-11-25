CREATE TABLE IF NOT EXISTS properties (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    address VARCHAR(500),
    city VARCHAR(255),
    description TEXT,
    owner_id BIGINT NOT NULL,
    INDEX idx_owner_id (owner_id),
    INDEX idx_city (city)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

