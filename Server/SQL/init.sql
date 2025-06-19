-- Détruire les tables existantes
DROP TABLE IF EXISTS attack_player_order_unit, attack_player_order, unit_creation_order, building_creation_order, player_unit, player_building, unit_type, building_type, player, game, user_token, user, race;

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
    defensive BOOLEAN NOT NULL,
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
    player_id BIGINT UNSIGNED NOT NULL,
    building_id BIGINT UNSIGNED NOT NULL,
    quantity INT NOT NULL,
    PRIMARY KEY (player_id, building_id),
    FOREIGN KEY (player_id) REFERENCES player(id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (building_id) REFERENCES building_type(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE player_unit (
    player_id BIGINT UNSIGNED NOT NULL,
    unit_id BIGINT UNSIGNED NOT NULL,
    quantity INT NOT NULL,
    PRIMARY KEY (player_id, unit_id),
    FOREIGN KEY (player_id) REFERENCES player(id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (unit_id) REFERENCES unit_type(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE building_creation_order (
    player_id BIGINT UNSIGNED NOT NULL,
    building_type_id BIGINT UNSIGNED NOT NULL,
    amount INT NOT NULL,
    PRIMARY KEY (player_id, building_type_id),
    FOREIGN KEY (player_id) REFERENCES player(id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (building_type_id) REFERENCES building_type(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE unit_creation_order (
    player_id BIGINT UNSIGNED NOT NULL,
    unit_type_id BIGINT UNSIGNED NOT NULL,
    amount INT NOT NULL,
    PRIMARY KEY (player_id, unit_type_id),
    FOREIGN KEY (player_id) REFERENCES player(id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (unit_type_id) REFERENCES unit_type(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE attack_player_order (
    id SERIAL PRIMARY KEY,
    player_id BIGINT UNSIGNED NOT NULL,
    target_player_id BIGINT UNSIGNED NOT NULL,
    FOREIGN KEY (player_id) REFERENCES player(id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (target_player_id) REFERENCES player(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE attack_player_order_unit (
    order_id BIGINT UNSIGNED NOT NULL,
    unit_type_id BIGINT UNSIGNED NOT NULL,
    amount INT NOT NULL,
    PRIMARY KEY (order_id, unit_type_id),
    FOREIGN KEY (order_id) REFERENCES attack_player_order(id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (unit_type_id) REFERENCES unit_type(id) ON UPDATE CASCADE ON DELETE CASCADE
);


-- Insertion des données statiques
INSERT INTO race (id, name, description, gold_multiplier, intelligence_multiplier, damage_multiplier, health_multiplier)
VALUES
    (1, 'Mort-Vivant', 'Individu mort qui revient à la vie', 1.30, 0.75, 1, 0.75),
    (2, 'Humain', 'Race équilibrée avec des compétences militaires et économiques', 1, 1.25, 0.75, 1),
    (3, 'Orc', 'Créature verte, de taille humanoïde mais plus musclée et pourvue de crocs implantés au hasard. Il a un goût prononcé pour la violence mais il est débile', 0.75, 0.50, 1.5, 1.25),
    (4, 'Elfe', 'Créature Mystique aux grandes oreilles possèdant une grande intelligence mais faible', 1, 2, 0.75, 0.75),
    (5, 'Nain', 'Homme de petite taille,robuste et possèdant ENORMEMENT d''or', 1.5, 1, 0.75, 1.25),
    (6, 'Nerlk', 'Mixte entre la force des orcs et l''intelligence des elfes le problèmes sont leurs économie', 0.5, 1, 1.25, 1.25),
    (7, 'Primotaures', 'Premières créatures du monde, les primotaures sont riches mais pacifiques', 1.5, 1, 0.60, 1);

INSERT INTO building_type (name, description, price, required_intelligence, gold_production, intelligence_production, resistance, max_units, initial_amount, accessible_race_id, defensive)
VALUES
    ('Carrière', 'Structure permettant d''extraire des ressources naturelles pour financer l''économie du royaume', 10, 0, 5, 0, 100, 0, 2, NULL, false),
    ('Mine', 'Structure permettant d''extraire de l''or pour financer l''économie du royaume mais nécessite de l''intelligence pour être débloquer', 25, 15, 25, 0, 120, 0, 0, NULL, false),
    ('Caserne', 'Bâtiment militaire utilisé pour entraîner et héberger des unités de combat', 10, 0, 0, 0, 100, 3, 1, NULL, false),
    ('Bibliothèque', 'Centre de savoir produisant de l''intelligence pour le développement des technologies', 10, 0, 0, 2, 80, 0, 0, NULL, false),
    ('Rempart','Sert de protection au royaume', 30, 10, 0, 0, 150, 0, 1, NULL, true),

    ('Cimetière', 'Lieu sacré des morts où les Mort-Vivants peuvent lever de nouvelles troupes', 15, 10, 0, 0, 80, 8, 0, 1, false),
    ('Nécropole','Ancienne ville de mage rempli de rituel sinistre',120 ,35 ,15 ,20 ,150 ,10 ,0 ,1, false),

    ('Église', 'Édifice spirituel dédié aux Humains, offrant protection et recrutement d''unités pieuses', 10, 12, 0, 0, 80, 8, 0, 2, false),
    ('Château', 'Résidence royale des Humains, servant de centre de commandement, lieu de commerce et offrant une grande protection', 100, 20, 30, 20, 250, 2, 0, 2, false),

    ('Donjon', 'Endroit qui respire la violence permettant de former des futur combattants', 20, 10, 0, 0, 70, 6, 0, 3, false),
    ('marché d''esclave', 'Endroit où les orcs achètent et vendent des esclaves', 150, 35, 50, 10, 100, 15, 0, 3, false),

    ('Tour de Mage', 'Endroit où les prochains mages sont formés', 20, 20, 1, 2, 170, 10, 0, 4, false),
    ('Arbre de Vie', 'Endroit où les elfes s''instruit', 180, 50, 50,20 , 200, 5, 0, 4, false),

    ('Mine de Nains', 'Mine d''or où les nains adultes passent 100% de leurs temps', 30, 6, 40, 0, 180, 0, 0, 5, false),
    ('Taverne de Nains','Bar dans lequel on recrute les futurs Nains', 130, 20, 0, 3, 100, 20, 0, 5, false),

    ('Tente', 'Endroit où les futurs combattants sont formés', 15, 4, 0, 0, 40, 8, 0, 6, false),
    ('Forge', 'Endroit où les Nerlk fabriquent leurs armes', 120, 25, 40, 5, 120, 0, 0, 6, false),

    ('Labyrinthe', 'Connu pour défendre le royaume des attaquants', 30, 6, 20, 3, 260, 1, 0, 7, false),
    ('Temple', 'Endroit où les Primotaures s''instruisent et se forme', 150, 30, 25, 30, 90, 20, 0, 7, false);

INSERT INTO unit_type (name, description, price, required_intelligence, health, damage, accessible_race_id)
VALUES
    ('Guerrier', 'Une unité de combat faible et polyvalente', 8.0, 0, 10.0, 20.0, NULL),

    ('Ingénieur de combat', 'Un combattant expert en ingénierie capable de construire et de réparer les infrastructures avec rapidité et efficacité', 15, 20, 30.0, 50.0, 2),
    ('Héros Légendaire','L''un des humains les plus puissant du monde', 220, 100.0, 110.0, 200.0, 2),

    ('Zombie', 'Une créature morte-vivante qui se déplace lentement, mais inflige des dégâts mortels avec ses griffes et ses morsures infectieuses', 20, 10, 10.0, 50.0, 1),
    ('Nécromancien','Un ancien héro humain devenue mort et invoquant la mort derrière lui', 250, 100, 100.0, 210.0, 1),

    ('Uruk Noir', 'Créature imposante et très puissante mais débile', 80, 6, 80.0, 30.0, 3),
    ('Chef des Orcs','Chefs des Orcs ils sont extrenement puissants', 200, 100, 200.0, 170.0, 3),

    ('Mage Elfique', 'Puissant mage', 80, 20, 15.0, 150.0, 4),
    ('Archer Elfique', 'Unité dangereuse des elfes',300,110, 100.0,300.0,4),

    ('Roi Mineur', 'Meilleurs des Nains', 20, 6, 35.0, 50.0, 5),
    ('Nain Ultime''Les meilleurs parmis les meilleurs des Rois mineurs', 'DESCRIPTION À RÉDIGER', 250, 100, 170.0, 150.0,5),

    ('Mage Orc', 'Issue de l''union entre elfes et orc, il est puissant mais petit', 30, 15, 15.0, 50.0, 6),
    ('Berserker Nerlk', 'Unité de combat Nerlk, très puissante mais coûteuse', 250, 120, 100.0, 150.0, 6),
    ('Minotaure', 'Issue du Labyrinthe ', 20, 20, 30.0, 50.0, 7),
    ('Dieu Primotaures', 'Dieu des Primotaures, très puissant', 350, 150, 200.0, 300.0, 7);
