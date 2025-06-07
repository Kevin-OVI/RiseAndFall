-- Créer les tables nécessaires
CREATE TABLE player_building (
                                 player_id SERIAL PRIMARY KEY,
                                 unit_id VARCHAR(255) NOT NULL,
                                 quantity INT NOT NULL,
                                 FOREIGN KEY (unit_id) REFERENCES unit_type(id) ON UPDATE CASCADE ON DELETE CASCADE
);