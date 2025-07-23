CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE user_authorities (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    authority VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, authority)
);

INSERT INTO users (username, password, email, enabled) VALUES
('admin', '$2a$10$zfXvZiTIGafbK56QvuPIcOVRnMW5tE7PAnom/1e73eovousD8ZEZK', 'admin@example.com', true),
('user', '$2a$10$yv0dE5tzP.UKFSwduRMYredce7YF10QQOei2MzHz0F6uVrJrHLz6K', 'user@example.com', true);

INSERT INTO user_authorities (user_id, authority) VALUES
(1, 'ROLE_ADMIN'),
(1, 'ROLE_USER'),
(2, 'ROLE_USER');