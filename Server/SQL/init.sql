-- Détruire les tables existantes
DROP TABLE IF EXISTS unit_creation_order, building_creation_order, unit_type, building_type, player, game, user_token, user, race;

-- Créer les tables nécessaires
CREATE TABLE race (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    gold_multiplier DECIMAL NOT NULL,
    intelligence_multiplier DECIMAL NOT NULL,
    damage_multiplier DECIMAL NOT NULL,
    health_multiplier DECIMAL NOT NULL

);

CREATE TABLE user (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
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
    price_gold INT NOT NULL,
    price_intelligence INT NOT NULL,
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
    price_gold INT NOT NULL,
    price_intelligence INT NOT NULL,
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
INSERT INTO race (id, name, description,gold_multiplier,intelligence_multiplier,damage_multiplier,health_multiplier)
VALUES
    (1, 'Mort-Vivant', 'individu mort qui revient a la vie',1.25,0.75,1,1),
    (2, 'Humain', 'Race équilibrée avec des compétences militaires et économiques',1,1.5,1,1),
    (3,'Orc','Créature verte, de taille humanoïde mais plus musclée et pourvue de crocs implantés au hasard. Il a un goût prononcé pour la violence',1,0.50,1.25,1),
    (4,'Elfe','Créature Mystique',1,2,1,0.5),
    (5,'Nain','Homme de petite taille mais robuste',1.5,1,0.5,1.5),
    (6,'Nerlk','Connais pas...',1,1,1,1),
    (7,'Primotaures','Asterion en est fan',1,1,1,1.75);

INSERT INTO building_type (name, description, price_gold,price_intelligence, gold_production, intelligence_production, max_units, initial_amount, accessible_race_id)
VALUES
    ('Carrière', 'Structure permettant d’extraire des ressources minérales pour financer l’économie du royaume', 5,0, 1, 0, 0, 4, NULL),
    ('Caserne', 'Bâtiment militaire utilisé pour entraîner et héberger des unités de combat', 10,0, 0, 0, 3, 1, NULL),
    ('Bibliothèque', 'Centre de savoir produisant de l’intelligence pour le développement des technologies', 10,10, 0, 2, 0, 0, NULL),
    ('Cimetière', 'Lieu sacré des morts où les Mort-Vivants peuvent lever de nouvelles troupes', 10,10, 0, 0, 2, 0, 1),
    ('Église', 'Édifice spirituel dédié aux Humains, offrant protection et recrutement d’unités pieuses', 10,10, 0, 0, 2, 0, 2),
    ('Donjon','Endroit qui respire la violence permettant de former des futur combattant',10,10,0,0,4,0,3),
    ('Tour de Mage','Endroit où on forme les prochains mages',10,10,1,5,1,1,4),
    ('Mine','Mine d''or où les nains adultes passent 100% de leurs temps',13,10,5,1,0,0,5),
    ('Tente','Endroit  où sont formés les futurs combattants',10,10,10,1,5,5,6),
    ('Labyrinte','Endroit Mystique',10,3,10,1,1,1,7);

INSERT INTO unit_type (name, description, price_gold,price_intelligence, health, damage, accessible_race_id) VALUES
     ('Guerrier', 'Une unité de combat robuste et polyvalente', 10,0, 100.0, 15.0, NULL),
     ('Génie', 'Un expert en ingénierie capable de construire et de réparer les infrastructures avec rapidité et efficacité', 10,10, 80.0, 12.0, 2),
     ('Zombie', 'Une créature morte-vivante qui se déplace lentement, mais inflige des dégâts mortels avec ses griffes et ses morsures infectieuses', 20,10, 70, 20, 1),
     ('Uruk Noir','Créature imposante et très puissant mais débile',30,10,150,30,3),
     ('Mage Elfique','Puissant mage',10,10,70,20,4),
     ('Roi Mineur','Meilleurs des Nains',13,10,170,15,5),
     ('Mage Orc','Issue de l''union entre elfes et orc il est puissant mais petit',15,10,100,25,6 ),
     ('Minotaure','Issue des labyrinthes',10,10,110,30,7);
