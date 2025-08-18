-- ========================================
-- DONNÉES DE TEST POUR LA TABLE USERS
-- ========================================

-- Nettoyage de la table (optionnel)
-- TRUNCATE TABLE users RESTART IDENTITY CASCADE;

-- ========================================
-- UTILISATEURS DE TEST
-- ========================================

-- 1. Administrateur principal
INSERT INTO users (username, email, password, role, is_active) VALUES
('admin', 'admin@example.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'ADMIN', true);

-- 2. Utilisateur normal 1
INSERT INTO users (username, email, password, role, is_active) VALUES
('john_doe', 'john.doe@email.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'USER', true);

-- 3. Utilisateur normal 2
INSERT INTO users (username, email, password, role, is_active) VALUES
('jane_smith', 'jane.smith@email.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'USER', true);

-- 4. Modérateur
INSERT INTO users (username, email, password, role, is_active) VALUES
('moderator1', 'mod1@example.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'MODERATOR', true);

- admin      / secret123  (ADMIN)
- john_doe   / secret123  (USER)
- jane_smith / secret123  (USER)
- moderator1 / secret123  (MODERATOR)

-- ========================================
-- VÉRIFICATION DES DONNÉES INSÉRÉES
-- ========================================

-- Compter le nombre total d'utilisateurs
SELECT COUNT(*) as total_users FROM users;

-- Compter par rôle
SELECT role, COUNT(*) as count FROM users GROUP BY role ORDER BY role;

-- Compter par statut
SELECT is_active, COUNT(*) as count FROM users GROUP BY is_active;

-- Afficher tous les utilisateurs actifs
SELECT id, username, email, role, created_at 
FROM users 
WHERE is_active = true 
ORDER BY created_at DESC;

-- Avions
INSERT INTO avion (pseudo, siege_business, siege_eco, date_fabrication)
VALUES 
  ('Airbus A320', 20, 150, '2010-05-15'),
  ('Boeing 737', 16, 180, '2012-11-20'),
  ('Embraer E190', 12, 100, '2015-07-30');

-- Villes
INSERT INTO ville (nom)
VALUES 
  ('Paris'),
  ('New York'),
  ('Tokyo');

-- Types de sièges
INSERT INTO type_siege (rubrique, prix_)
VALUES 
  ('Economique', 300.00),
  ('Business', 1200.00);

-- === Vols ===

INSERT INTO vol (numero_vol_, date_vol_, Id_ville, Id_avion)
VALUES 
  ('AF123', '2025-09-01 10:30:00', 1, 1),  
  ('DL456', '2025-09-02 14:00:00', 2, 2),  
  ('JL789', '2025-09-05 22:15:00', 3, 3);  

-- === Prix par type de siège pour chaque vol ===

-- Vol 1
INSERT INTO prix_siege_vol_ (prix_, Id_type_siege, Id_vol)
VALUES 
  (300.00, 1, 1),
  (1200.00, 2, 1);

-- Vol 2
INSERT INTO prix_siege_vol_ (prix_, Id_type_siege, Id_vol)
VALUES 
  (350.00, 1, 2),
  (1300.00, 2, 2);

-- Vol 3
INSERT INTO prix_siege_vol_ (prix_, Id_type_siege, Id_vol)
VALUES 
  (400.00, 1, 3),
  (1500.00, 2, 3);
