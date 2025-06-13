-- Détruire les tables existantes
DROP TABLE IF EXISTS attack_player_order_unit, attack_player_order, unit_creation_order, building_creation_order, unit_type, building_type, player, game, user_token, user, race;

-- Créer les tables nécessaires
CREATE TABLE race (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    gold_multiplier DECIMAL(10, 2) NOT NULL,
    intelligence_multiplier DECIMAL(10, 2) NOT NULL,
    damage_multiplier DECIMAL(10, 2) NOT NULL,
    health_multiplier DECIMAL(10, 2) NOT NULL
);

CREATE TABLE user (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL
);

CREATE TABLE user_token (
    id SERIAL PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    token VARCHAR(255) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE game (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    turn_interval INT NOT NULL DEFAULT 15,
    current_turn INT NOT NULL DEFAULT 1,
    min_players INT NOT NULL DEFAULT 3,
    max_players INT NOT NULL DEFAULT 30,
    password_hash VARCHAR(255) DEFAULT NULL,
    state ENUM('WAITING', 'RUNNING', 'ENDED') NOT NULL DEFAULT 'WAITING',
    next_action_at TIMESTAMP DEFAULT NULL
);

CREATE TABLE player (
    id SERIAL PRIMARY KEY,
    user_id BIGINT UNSIGNED DEFAULT NULL,
    game_id BIGINT UNSIGNED NOT NULL,
    race_id BIGINT UNSIGNED NOT NULL,
    gold DECIMAL(10, 2) NOT NULL DEFAULT 50,
    intelligence DECIMAL(10, 2)  NOT NULL DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES user(id) ON UPDATE CASCADE ON DELETE SET NULL,
    FOREIGN KEY (game_id) REFERENCES game(id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (race_id) REFERENCES race(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE building_type (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    required_intelligence DECIMAL(10, 2) NOT NULL DEFAULT 0,
    gold_production DECIMAL(10, 2) NOT NULL,
    intelligence_production DECIMAL(10, 2) NOT NULL,
    resistance DECIMAL(10, 2) NOT NULL,
    max_units INT NOT NULL,
    initial_amount INT NOT NULL,
    accessible_race_id BIGINT UNSIGNED DEFAULT NULL COMMENT 'NULL si accessible à toutes les races, renseigné si accessible uniquement par une race particulière. Suppression en cascade pour ne pas rendre accessible à tous les bâtiments privés.',
    FOREIGN KEY (accessible_race_id) REFERENCES race(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE unit_type (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    required_intelligence DECIMAL(10, 2) NOT NULL,
    health DECIMAL(10, 2) NOT NULL,
    damage DECIMAL(10, 2) NOT NULL,
    accessible_race_id BIGINT UNSIGNED DEFAULT NULL COMMENT 'NULL si accessible à toutes les races, renseigné si accessible uniquement par une race particulière. Suppression en cascade pour ne pas rendre accessible à tous les bâtiments privés.',
    FOREIGN KEY (accessible_race_id) REFERENCES race(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE player_building (
    id SERIAL PRIMARY KEY,
    player_id BIGINT UNSIGNED NOT NULL,
    building_id BIGINT UNSIGNED NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (player_id) REFERENCES player(id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (building_id) REFERENCES building_type(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE player_unit (
    id SERIAL PRIMARY KEY,
    player_id BIGINT UNSIGNED NOT NULL,
    unit_id BIGINT UNSIGNED NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (player_id) REFERENCES player(id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (unit_id) REFERENCES unit_type(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE building_creation_order (
    id SERIAL PRIMARY KEY,
    player_id BIGINT UNSIGNED NOT NULL,
    building_type_id BIGINT UNSIGNED NOT NULL,
    amount INT NOT NULL,
    FOREIGN KEY (player_id) REFERENCES player(id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (building_type_id) REFERENCES building_type(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE unit_creation_order (
    id SERIAL PRIMARY KEY,
    player_id BIGINT UNSIGNED NOT NULL,
    unit_type_id BIGINT UNSIGNED NOT NULL,
    amount INT NOT NULL,
    FOREIGN KEY (player_id) REFERENCES player(id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (unit_type_id) REFERENCES unit_type(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE attack_player_order (
    id SERIAL PRIMARY KEY,
    player_id BIGINT UNSIGNED NOT NULL,
    target_player_id BIGINT UNSIGNED NOT NULL,
    FOREIGN KEY (player_id) REFERENCES player(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE attack_player_order_unit (
    id SERIAL PRIMARY KEY,
    order_id BIGINT UNSIGNED NOT NULL,
    unit_type_id BIGINT UNSIGNED NOT NULL,
    amount INT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES attack_player_order(id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (unit_type_id) REFERENCES unit_type(id) ON UPDATE CASCADE ON DELETE CASCADE
);


-- Insertion des données statiques
INSERT INTO race (id, name, description, gold_multiplier, intelligence_multiplier, damage_multiplier, health_multiplier)
VALUES
    (1, 'Mort-Vivant', 'Individu mort qui revient à la vie', 1.25, 0.75, 1, 0.75),
    (2, 'Humain', 'Race équilibrée avec des compétences militaires et économiques', 1, 1.25, 0.75, 1),
    (3, 'Orc', 'Créature verte, de taille humanoïde mais plus musclée et pourvue de crocs implantés au hasard. Il a un goût prononcé pour la violence mais il est débile', 0.75, 0.50, 1.5, 1.25),
    (4, 'Elfe', 'Créature Mystique aux grandes oreilles possèdant une grande intelligence mais faible', 1, 2, 1, 0.75),
    (5, 'Nain', 'Homme de petite taille,robuste et possèdant ENORMEMENT d''or', 1.5, 1, 0.75, 1.5),
    (6, 'Nerlk', 'Mixte entre la force des orcs et l''intelligence des elfes le problèmes sont leurs économie', 0.5, 1, 1.25, 1.25),
    (7, 'Primotaures', 'Premières créatures du monde, les primotaures sont riches mais pacifiques', 1.5, 1, 0.75, 1);

INSERT INTO building_type (name, description, price, required_intelligence, gold_production, intelligence_production, resistance, max_units, initial_amount, accessible_race_id)
VALUES
    ('Carrière', 'Structure permettant d''extraire des ressources naturelles pour financer l''économie du royaume', 10, 0, 5, 0, 100, 0, 4, NULL),
    ('Mine', 'Structure permettant d''extraire de l''or pour financer l''économie du royaume mais nécessite de l''intelligence pour être débloquer', 20, 15, 10, 0, 120, 0, 0, NULL),
    ('Caserne', 'Bâtiment militaire utilisé pour entraîner et héberger des unités de combat', 10, 0, 0, 0, 100, 3, 1, NULL),
    ('Bibliothèque', 'Centre de savoir produisant de l''intelligence pour le développement des technologies', 10, 0, 0, 4, 80, 0, 1, NULL),
    ('Rampart','Sert de protection au royaume', 30, 10, 0, 0, 50, 0, 1, NULL),
    ('Cimetière', 'Lieu sacré des morts où les Mort-Vivants peuvent lever de nouvelles troupes', 15, 10, 0, 0, 80, 15, 0, 1),
    ('Tour obscur','Ancienne tour de mage rempli de rituel sinistre',40 ,4 ,0 ,20 ,150 ,0 ,0 ,1),
    ('Église', 'Édifice spirituel dédié aux Humains, offrant protection et recrutement d''unités pieuses', 10, 6, 0, 0, 80, 5, 0, 2),
    ('Château', 'Résidence royale des Humains, servant de centre de commandement, lieu de commerce et offrant une grande protection', 50, 20, 10, 1, 250, 0, 0, 2),
    ('Donjon', 'Endroit qui respire la violence permettant de former des futur combattants', 50, 2, 0, 0, 70, 10, 0, 3),
    ('marché aux esclaves', 'Endroit où les orcs achètent et vendent des esclaves', 20, 15, 25, 0, 100, 0, 0, 3),
    ('Tour de Mage', 'Endroit où les prochains mages sont formés', 50, 70, 1, 5, 170, 1, 0, 4),
    ('Arbre de Vie', 'Endroit où les elfes s''instruit', 10, 2, 0,20 , 100, 0, 0, 4),
    ('Mine de Nains', 'Mine d''or où les nains adultes passent 100% de leurs temps', 30, 6, 20, 0, 180, 0, 0, 5),
    ('Taverne de Nains','Bar dans lequel on recrute les futurs Nains', 40, 15, 0, 0, 100, 10, 0, 5 ),
    ('Tente', 'Endroit où les futurs combattants sont formés', 10, 4, 0, 0, 40, 15, 0, 6),
    ('Forge', 'Endroit où les Nerlk fabriquent leurs armes', 20, 20, 40, 0, 120, 0, 0, 6),
    ('Labyrinthe', 'Connu pour défendre le royaume des attaquants', 80, 4, 0, 0, 260, 1, 0, 7),
    ('Temple', 'Endroit où les Primotaures s''instruisent et se forme', 100, 12, 10, 30, 90, 20, 0, 7);

INSERT INTO unit_type (name, description, price, required_intelligence, health, damage, accessible_race_id)
VALUES
    ('Guerrier', 'Une unité de combat faible et polyvalente', 8.0, 0, 10.0, 20.0, NULL),
    ('Ingénieur de combat', 'Un combattant expert en ingénierie capable de construire et de réparer les infrastructures avec rapidité et efficacité', 20, 20, 30.0, 50.0, 2),
    ('Héros Légendaire','L''un des humains les plus puissant du monde', 150, 40.0, 110.0, 200.0, 2),
    ('Zombie', 'Une créature morte-vivante qui se déplace lentement, mais inflige des dégâts mortels avec ses griffes et ses morsures infectieuses', 30, 10, 10.0, 35.0, 1),
    ('Nécromancien','Un ancien héro humain devenue mort et invoquant la mort derrière lui', 150, 40, 100.0, 210.0, 1),
    ('Uruk Noir', 'Créature imposante et très puissante mais débile', 100, 10, 80.0, 30.0, 3),
    ('Chef des Orcs','Chefs des Orcs ils sont extrenement puissants', 150, 20, 200.0, 100.0, 3),
    ('Mage Elfique', 'Puissant mage', 80, 20, 15.0, 150.0, 4),
    ('Archer Elfique', 'Unité dangereuse des elfes',150,40, 100.0,200.0,4),
    ('Roi Mineur', 'Meilleurs des Nains', 50, 6, 35.0, 55.0, 5),
    ('Nain Ultime''Les meilleurs parmis les meilleurs des Rois mineurs', 'DESCRIPTION À RÉDIGER', 150, 20, 100.0, 150.0,5),
    ('Mage Orc', 'Issue de l''union entre elfes et orc, il est puissant mais petit', 40, 15, 15.0, 100.0, 6),
    ('Berserker Nerlk', 'Unité de combat Nerlk, très puissante mais coûteuse', 150, 20, 110.0, 150.0, 6),
    ('Minotaure', 'Issue du Labyrinthe ', 20, 20, 30.0, 70.0, 7),
    ('Dieu Primotaures', 'Dieu des Primotaures, très puissant', 150, 50, 200.0, 300.0, 7);
