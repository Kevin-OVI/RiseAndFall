SET foreign_key_checks = 0;

DROP TABLE IF EXISTS `order`;
DROP TABLE IF EXISTS unit_type;
DROP TABLE IF EXISTS building_type;
DROP TABLE IF EXISTS `player`;
DROP TABLE IF EXISTS user_token;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS game;
DROP TABLE IF EXISTS race;

CREATE TABLE race (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    description VARCHAR(255)
);

CREATE TABLE user (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255),
    email VARCHAR(255),
    password_hash VARCHAR(255),
    race_id BIGINT UNSIGNED NOT NULL,
    FOREIGN KEY (race_id) REFERENCES race(id)
);

CREATE TABLE user_token (
    id SERIAL PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    token VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE game (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    day_time INT DEFAULT 15,
    nb_max_player INT DEFAULT 30,
    current_day INT DEFAULT 0,
    password_hash VARCHAR(255) DEFAULT NULL
);

CREATE TABLE player (
    id SERIAL PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    game_id BIGINT UNSIGNED NOT NULL,
    gold INT DEFAULT 50,
    intelligence INT DEFAULT 50,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (game_id) REFERENCES game(id)
);

CREATE TABLE building_type (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    description VARCHAR(255),
    price INT,
    gold_production INT,
    intelligence_production INT,
    max_units INT,
    initial_amount INT,
    accessible_race_id BIGINT UNSIGNED,
    FOREIGN KEY (accessible_race_id) REFERENCES race(id)
);

CREATE TABLE unit_type (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    description VARCHAR(255),
    price INT,
    health INT,
    damage INT,
    accessible_race_id BIGINT UNSIGNED,
    FOREIGN KEY (accessible_race_id) REFERENCES race(id)
);

CREATE TABLE `order` (
    id SERIAL PRIMARY KEY,
    player_id BIGINT UNSIGNED NOT NULL,
    building_id BIGINT UNSIGNED NOT NULL,
    unit_id BIGINT UNSIGNED NOT NULL,
    amount INT,
    FOREIGN KEY (player_id) REFERENCES player(id),
    FOREIGN KEY (building_id) REFERENCES building_type(id),
    FOREIGN KEY (unit_id) REFERENCES unit_type(id)
);

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


SET foreign_key_checks = 1;