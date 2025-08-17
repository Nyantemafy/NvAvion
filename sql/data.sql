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