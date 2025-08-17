-- Création de la base de données
CREATE DATABASE login_db;

-- Se connecter à la base login_db
\c login_db;

-- Création de la table users
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- Index pour améliorer les performances de recherche
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);

-- Contrainte pour s'assurer que le rôle est valide
ALTER TABLE users ADD CONSTRAINT chk_role 
    CHECK (role IN ('USER', 'ADMIN', 'MODERATOR'));