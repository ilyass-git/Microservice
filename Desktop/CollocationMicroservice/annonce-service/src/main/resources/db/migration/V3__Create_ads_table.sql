CREATE TABLE IF NOT EXISTS ads (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    property_id BIGINT NOT NULL,
    room_id BIGINT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    owner_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    INDEX idx_property_id (property_id),
    INDEX idx_room_id (room_id),
    INDEX idx_owner_id (owner_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS ad_photo_urls (
    ad_id BIGINT NOT NULL,
    photo_urls VARCHAR(500),
    INDEX idx_ad_id (ad_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



