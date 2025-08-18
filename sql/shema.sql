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

CREATE TABLE avion(
   Id_avion SERIAL,
   pseudo VARCHAR(50) ,
   siege_business INTEGER,
   siege_eco INTEGER,
   date_fabrication DATE,
   PRIMARY KEY(Id_avion)
);

CREATE TABLE ville(
   Id_ville SERIAL,
   nom VARCHAR(50) ,
   PRIMARY KEY(Id_ville)
);

CREATE TABLE type_siege(
   Id_type_siege SERIAL,
   rubrique VARCHAR(50) ,
   prix_ NUMERIC(15,2)  ,
   PRIMARY KEY(Id_type_siege)
);

CREATE TABLE vol(
   Id_vol SERIAL,
   numero_vol_ VARCHAR(50) ,
   date_vol_ TIMESTAMP,
   Id_ville INTEGER NOT NULL,
   Id_avion INTEGER,
   PRIMARY KEY(Id_vol),
   FOREIGN KEY(Id_ville) REFERENCES ville(Id_ville),
   FOREIGN KEY(Id_avion) REFERENCES avion(Id_avion)
);

CREATE TABLE promotion(
   Id_promotion SERIAL,
   nom VARCHAR(50) ,
   date_debut DATE,
   date_fin DATE,
   reduction_pourcentage_ INTEGER,
   Id_vol INTEGER,
   PRIMARY KEY(Id_promotion),
   FOREIGN KEY(Id_vol) REFERENCES vol(Id_vol)
);

CREATE TABLE prix_siege_vol_(
   Id_prix_siege_vol_ SERIAL,
   prix_ NUMERIC(15,2)  ,
   Id_type_siege INTEGER NOT NULL,
   Id_vol INTEGER NOT NULL,
   PRIMARY KEY(Id_prix_siege_vol_),
   FOREIGN KEY(Id_type_siege) REFERENCES type_siege(Id_type_siege),
   FOREIGN KEY(Id_vol) REFERENCES vol(Id_vol)
);
