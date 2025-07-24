-- Habilita a extensão uuid-ossp se ainda não estiver habilitada
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Criação da tabela users com UUID
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);

-- Criação da tabela user_authorities com UUID
CREATE TABLE user_authorities (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    authority VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, authority)
);

-- Inserção dos usuários com UUID
INSERT INTO users (id, username, password, email, enabled) VALUES
('22ab27db-e446-403a-bd43-b727dba7c534', 'admin', '$2a$10$zfXvZiTIGafbK56QvuPIcOVRnMW5tE7PAnom/1e73eovousD8ZEZK', 'admin@example.com', true),
('c0e29311-4adf-48ab-b6a4-243cc0d7bfa6', 'user', '$2a$10$yv0dE5tzP.UKFSwduRMYredce7YF10QQOei2MzHz0F6uVrJrHLz6K', 'user@example.com', true);

-- Inserção das authorities
INSERT INTO user_authorities (user_id, authority) VALUES
('22ab27db-e446-403a-bd43-b727dba7c534', 'ROLE_ADMIN'),
('22ab27db-e446-403a-bd43-b727dba7c534', 'ROLE_USER'),
('c0e29311-4adf-48ab-b6a4-243cc0d7bfa6', 'ROLE_USER');