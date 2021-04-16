/* Populate tables clients */
INSERT INTO clients (first_name, last_name, email, created_at, updated_at, photo, password, enabled) VALUES('Manuel', 'Ibañez', 'demonk7@gmail.com', NOW(), NOW(), '', '$2a$10$jjEHyJ/eU9FVg17.aOlcNe9leh2tq4iwvlgxNNoHM3BoHt5Xm6KMC', 1);

/* Populate tables clients */
INSERT INTO authorities (client_id, authority) VALUES(1, 'ROLE_USER');
INSERT INTO authorities (client_id, authority) VALUES(1, 'ROLE_ADMIN');
INSERT INTO authorities (client_id, authority) VALUES(2, 'ROLE_USER');

/* Populate tables categories */
INSERT INTO categories (name, created_at, updated_at) VALUES('Aperitivo', NOW(), NOW());
INSERT INTO categories (name, created_at, updated_at) VALUES('Bebidas', NOW(), NOW());
INSERT INTO categories (name, created_at, updated_at) VALUES('Desayuno', NOW(), NOW());
INSERT INTO categories (name, created_at, updated_at) VALUES('Ensaladas', NOW(), NOW());
INSERT INTO categories (name, created_at, updated_at) VALUES('Plato principal', NOW(), NOW());
INSERT INTO categories (name, created_at, updated_at) VALUES('Postres', NOW(), NOW());
INSERT INTO categories (name, created_at, updated_at) VALUES('Carnes', NOW(), NOW());
INSERT INTO categories (name, created_at, updated_at) VALUES('Pescados', NOW(), NOW());
INSERT INTO categories (name, created_at, updated_at) VALUES('Salsas', NOW(), NOW());
INSERT INTO categories (name, created_at, updated_at) VALUES('Sopas', NOW(), NOW());
INSERT INTO categories (name, created_at, updated_at) VALUES('Carnes', NOW(), NOW());
INSERT INTO categories (name, created_at, updated_at) VALUES('Arroces', NOW(), NOW());
INSERT INTO categories (name, created_at, updated_at) VALUES('Pastas', NOW(), NOW());
INSERT INTO categories (name, created_at, updated_at) VALUES('Legumbres', NOW(), NOW());
INSERT INTO categories (name, created_at, updated_at) VALUES('Entrantes', NOW(), NOW());
INSERT INTO categories (name, created_at, updated_at) VALUES('Tapitas', NOW(), NOW());
INSERT INTO categories (name, created_at, updated_at) VALUES('Horneados', NOW(), NOW());
INSERT INTO categories (name, created_at, updated_at) VALUES('Acompañamientos', NOW(), NOW());

/* Populate tables measures */
INSERT INTO ingredients_measures (name, created_at, updated_at) VALUES('unidad', NOW(), NOW());
INSERT INTO ingredients_measures (name, created_at, updated_at) VALUES('kg', NOW(), NOW());
INSERT INTO ingredients_measures (name, created_at, updated_at) VALUES('g', NOW(), NOW());
INSERT INTO ingredients_measures (name, created_at, updated_at) VALUES('l', NOW(), NOW());
INSERT INTO ingredients_measures (name, created_at, updated_at) VALUES('cl', NOW(), NOW());
INSERT INTO ingredients_measures (name, created_at, updated_at) VALUES('ml', NOW(), NOW());
INSERT INTO ingredients_measures (name, created_at, updated_at) VALUES('cuchara sopera', NOW(), NOW());
INSERT INTO ingredients_measures (name, created_at, updated_at) VALUES('cuchara de cafe', NOW(), NOW());
INSERT INTO ingredients_measures (name, created_at, updated_at) VALUES('vaso', NOW(), NOW());
INSERT INTO ingredients_measures (name, created_at, updated_at) VALUES('taza', NOW(), NOW());