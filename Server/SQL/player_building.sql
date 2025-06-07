-- Créer les tables nécessaires
CREATE TABLE player_building (
                      player_id SERIAL PRIMARY KEY,
                      building_id VARCHAR(255) NOT NULL,
                      quantity INT NOT NULL,
                      FOREIGN KEY (building_id) REFERENCES building_type(id) ON UPDATE CASCADE ON DELETE CASCADE
);