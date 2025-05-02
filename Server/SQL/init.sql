-- Détruire les tables existantes
DROP TABLE IF EXISTS unit_creation_order;
DROP TABLE IF EXISTS building_creation_order;
DROP TABLE IF EXISTS unit_type;
DROP TABLE IF EXISTS building_type;
DROP TABLE IF EXISTS player;
DROP TABLE IF EXISTS game;
DROP TABLE IF EXISTS user_token;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS race;


-- Créer les tables nécessaires
CREATE TABLE race (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL
);

CREATE TABLE user (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL
);

CREATE TABLE user_token (
    id SERIAL PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    token VARCHAR(255) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE game (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    turn_interval INT NOT NULL DEFAULT 15,
    current_turn INT NOT NULL DEFAULT 0,
    min_players INT NOT NULL DEFAULT 3,
    max_players INT NOT NULL DEFAULT 30,
    password_hash VARCHAR(255) DEFAULT NULL,
    state ENUM('WAITING', 'RUNNING', 'ENDED') NOT NULL DEFAULT 'WAITING',
    last_turn_at TIMESTAMP DEFAULT NULL
);

CREATE TABLE player (
    id SERIAL PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    game_id BIGINT UNSIGNED NOT NULL,
    race_id BIGINT UNSIGNED NOT NULL,
    gold INT NOT NULL DEFAULT 50,
    intelligence INT NOT NULL DEFAULT 50,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (game_id) REFERENCES game(id),
    FOREIGN KEY (race_id) REFERENCES race(id)
);

CREATE TABLE building_type (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    price INT NOT NULL,
    gold_production INT NOT NULL,
    intelligence_production INT NOT NULL,
    max_units INT NOT NULL,
    initial_amount INT NOT NULL,
    accessible_race_id BIGINT UNSIGNED,
    FOREIGN KEY (accessible_race_id) REFERENCES race(id)
);

CREATE TABLE unit_type (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    price INT NOT NULL,
    health INT NOT NULL,
    damage INT NOT NULL,
    accessible_race_id BIGINT UNSIGNED,
    FOREIGN KEY (accessible_race_id) REFERENCES race(id)
);

CREATE TABLE building_creation_order (
    id SERIAL PRIMARY KEY,
    player_id BIGINT UNSIGNED NOT NULL,
    building_type_id BIGINT UNSIGNED NOT NULL,
    amount INT NOT NULL,
    FOREIGN KEY (player_id) REFERENCES player(id),
    FOREIGN KEY (building_type_id) REFERENCES building_type(id)
);

CREATE TABLE unit_creation_order (
    id SERIAL PRIMARY KEY,
    player_id BIGINT UNSIGNED NOT NULL,
    unit_id BIGINT UNSIGNED NOT NULL,
    amount INT NOT NULL,
    FOREIGN KEY (player_id) REFERENCES player(id),
    FOREIGN KEY (unit_id) REFERENCES unit_type(id)
);


-- Insertion des données statiques
INSERT INTO race (id, name, description)
VALUES
    (1, 'Mort-Vivant', 'individu mort qui revient a la vie'),
    (2, 'Humain', 'Race équilibrée avec des compétences militaires et économiques');

INSERT INTO building_type (name, description, price, gold_production, intelligence_production, max_units, initial_amount, accessible_race_id)
VALUES
    ('Carrière', 'Structure permettant d’extraire des ressources minérales pour financer l’économie du royaume', 5, 1, 0, 0, 4, NULL),
    ('Caserne', 'Bâtiment militaire utilisé pour entraîner et héberger des unités de combat', 10, 0, 0, 3, 1, NULL),
    ('Bibliothèque', 'Centre de savoir produisant de l’intelligence pour le développement des technologies', 10, 0, 2, 0, 0, NULL),
    ('Cimetière', 'Lieu sacré des morts où les Mort-Vivants peuvent lever de nouvelles troupes', 10, 0, 0, 2, 0, 1),
    ('Église', 'Édifice spirituel dédié aux Humains, offrant protection et recrutement d’unités pieuses', 10, 0, 0, 2, 0, 2);

INSERT INTO unit_type (name, description, price, health, damage, accessible_race_id) VALUES
     ('Guerrier', 'Une unité de combat robuste et polyvalente', 10, 100.0, 15.0, NULL),
     ('Génie', 'Un expert en ingénierie capable de construire et de réparer les infrastructures avec rapidité et efficacité', 10, 80.0, 12.0, NULL),
     ('Zombie', 'Une créature morte-vivante qui se déplace lentement, mais inflige des dégâts mortels avec ses griffes et ses morsures infectieuses', 20, 70, 20, 1);
