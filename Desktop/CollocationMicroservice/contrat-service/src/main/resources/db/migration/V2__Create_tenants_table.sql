CREATE TABLE IF NOT EXISTS tenants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    contract_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    room_id BIGINT,
    INDEX idx_contract_id (contract_id),
    INDEX idx_user_id (user_id),
    INDEX idx_room_id (room_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



