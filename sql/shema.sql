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

ALTER TABLE vol 
    ALTER COLUMN id_avion TYPE BIGINT 
    USING id_avion::bigint;

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

CREATE TABLE reservation(
   Id_reservation SERIAL,
   date_reservation_ TIMESTAMP,
   prix_total_ NUMERIC(15,2)  ,
   Id_vol INTEGER NOT NULL,
   Id_user INTEGER,
   siege_business INTEGER,
   siege_eco INTEGER,
   PRIMARY KEY(Id_reservation),
   FOREIGN KEY(Id_vol) REFERENCES vol(Id_vol),
   FOREIGN KEY(Id_user) REFERENCES users(id)
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

CREATE TABLE annulation_reservation_(
   Id_annulation_reservation_ SERIAL,
   raison_annulation_ VARCHAR(50) ,
   date_annulation_ DATE,
   Id_reservation INTEGER,
   PRIMARY KEY(Id_annulation_reservation_),
   FOREIGN KEY(Id_reservation) REFERENCES reservation(Id_reservation)
);

ALTER TABLE users ADD COLUMN date_naissance DATE;
ALTER TABLE users ADD COLUMN id_categorie_age INT;

CREATE TABLE categorie_age (
    id_categorie_age SERIAL PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    age_min INTEGER NOT NULL,
    age_max INTEGER,
    multiplicateur_prix NUMERIC(5,4) NOT NULL DEFAULT 1.0000,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE prix_age_vol (
    id_prix_age_vol SERIAL PRIMARY KEY,
    id_vol INTEGER NOT NULL,
    id_type_siege INTEGER NOT NULL,
    id_categorie_age INTEGER NOT NULL,
    prix_base NUMERIC(15,2) NOT NULL,
    multiplicateur NUMERIC(6,2) NOT NULL, -- tu stockes ici le multiplicateur
    prix_final NUMERIC(15,2) GENERATED ALWAYS AS (prix_base * multiplicateur) STORED,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(id_vol) REFERENCES vol(id_vol) ON DELETE CASCADE,
    FOREIGN KEY(id_type_siege) REFERENCES type_siege(id_type_siege),
    FOREIGN KEY(id_categorie_age) REFERENCES categorie_age(id_categorie_age),
    UNIQUE(id_vol, id_type_siege, id_categorie_age)
);

ALTER TABLE prix_age_vol 
ALTER COLUMN id_prix_age_vol TYPE BIGINT;

CREATE VIEW v_user_age_category AS
SELECT 
    u.id,
    u.username,
    u.date_naissance,
    EXTRACT(YEAR FROM AGE(CURRENT_DATE, u.date_naissance)) AS age,
    ca.id_categorie_age,
    ca.nom AS categorie_nom,
    ca.multiplicateur_prix
FROM users u
LEFT JOIN categorie_age ca ON (
    EXTRACT(YEAR FROM AGE(CURRENT_DATE, u.date_naissance)) >= ca.age_min 
    AND (ca.age_max IS NULL OR EXTRACT(YEAR FROM AGE(CURRENT_DATE, u.date_naissance)) <= ca.age_max)
    AND ca.is_active = TRUE
)
WHERE u.date_naissance IS NOT NULL;

CREATE VIEW v_prix_final_user AS
SELECT 
    v.id_vol,
    v.numero_vol_,
    ts.id_type_siege,
    ts.rubrique AS type_siege,
    u.id AS user_id,
    u.username,
    uac.age,
    uac.categorie_nom,
    pav.prix_base,
    pav.prix_final,
    uac.multiplicateur_prix
FROM vol v
CROSS JOIN type_siege ts
CROSS JOIN v_user_age_category uac
JOIN users u ON u.id_categorie_age = uac.id_categorie_age 
LEFT JOIN prix_age_vol pav ON (
    pav.id_vol = v.id_vol 
    AND pav.id_type_siege = ts.id_type_siege 
    AND pav.id_categorie_age = uac.id_categorie_age
);

CREATE OR REPLACE FUNCTION calculer_prix_age(
    p_id_vol INTEGER,
    p_id_type_siege INTEGER,
    p_date_naissance DATE
) RETURNS NUMERIC(15,2) AS $$
DECLARE
    v_age INTEGER;
    v_prix_final NUMERIC(15,2);
BEGIN
    v_age := EXTRACT(YEAR FROM AGE(CURRENT_DATE, p_date_naissance));
    
    SELECT pav.prix_final INTO v_prix_final
    FROM prix_age_vol pav
    JOIN categorie_age ca ON ca.id_categorie_age = pav.id_categorie_age
    WHERE pav.id_vol = p_id_vol
    AND pav.id_type_siege = p_id_type_siege
    AND v_age >= ca.age_min
    AND (ca.age_max IS NULL OR v_age <= ca.age_max)
    AND ca.is_active = TRUE
    LIMIT 1;
    
    IF v_prix_final IS NULL THEN
        SELECT psv.prix_ INTO v_prix_final
        FROM prix_siege_vol_ psv
        WHERE psv.id_vol = p_id_vol
        AND psv.id_type_siege = p_id_type_siege
        LIMIT 1;
    END IF;
    
    RETURN COALESCE(v_prix_final, 0);
END;
$$ LANGUAGE plpgsql;

-- 8. Index pour améliorer les performances
CREATE INDEX idx_prix_age_vol_vol_siege ON prix_age_vol(id_vol, id_type_siege);
CREATE INDEX idx_prix_age_vol_categorie ON prix_age_vol(id_categorie_age);
CREATE INDEX idx_users_date_naissance ON users(date_naissance);
