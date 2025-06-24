package fr.butinfoalt.riseandfall.front.description;

import fr.butinfoalt.riseandfall.front.View;
import fr.butinfoalt.riseandfall.front.util.UIUtils;
import javafx.beans.InvalidationListener;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * Classe représentant la scène de description du jeu.
 * Elle affiche une image de fond et un texte descriptif.
 */
public class DescriptionStage extends Stage {
    public static final DescriptionStage INSTANCE = new DescriptionStage();

    /**
     * Constructeur de la scène de description.
     * Il initialise la taille minimale de la fenêtre, défini le titre et la scène de contenu.
     */
    private DescriptionStage() {
        this.setMinWidth(256);
        this.setMinHeight(192);
        this.setTitle(View.DESCRIPTION.getWindowTitle());
        this.setScene(View.DESCRIPTION.getScene(1024, 768, this::setupScene));
    }

    /**
     * Méthode pour configurer la scène de description.
     * Elle définit l'image de fond, adapte la taille de l'image à la fenêtre,
     * et centre le texte dans le ScrollPane.
     *
     * @param scene La scène à configurer.
     */
    private void setupScene(Scene scene) {
        DescriptionController controller = View.DESCRIPTION.getController();

        UIUtils.setBackgroundImage("images/map.jpg", scene, controller.backgroundImageView);
        Text mainTitle = new Text("Rise & Fall\n");
        mainTitle.setTextAlignment(TextAlignment.CENTER);
        mainTitle.setStyle("-fx-font-size: 36px; -fx-font-weight: bold;");

        // Texte formaté avec des titres en gras
        Text intro1 = new Text("Rise & Fall est un jeu développé par une équipe de choc.\n");
        Text intro2 = new Text("Le but est de créer un jeu tour par tour dans un monde fantasy.\n");
        Text intro3 = new Text("On a présenté le projet, maintenant passons aux règles du jeu :\n\n");

        Text objectifTitre = new Text("Objectif de jeu :\n");
        objectifTitre.setStyle("-fx-font-weight: bold");

        Text objectifTexte = new Text(
                """
                        Dans Rise & Fall, chaque joueur incarne une civilisation dans un monde fantasy. \
                        Le but est de faire prospérer sa civilisation en gérant ses ressources, en développant son économie, \
                        et en étendant son territoire tout en survivant jusqu’à la fin de la partie.

                        """);

        Text toursTitre = new Text("Déroulement des tours :\n");
        toursTitre.setStyle("-fx-font-weight: bold");

        Text toursTexte = new Text(
                """
                        • Le jeu se joue au tour par tour.
                        • À chaque tour, un joueur peut :
                          + Collecter des ressources
                          + Construire des bâtiments
                          + Recruter des unités
                          + Combattre des unités ennemies
                          + Attaquer ou interagir avec d’autres joueurs 

                        """);

        Text finTitre = new Text("Fin de partie\n");
        finTitre.setStyle("-fx-font-weight: bold");

        Text finTexte = new Text(
                """
                        Opportunité de gagner la partie :
                        • La partie se termine lorsqu’il ne reste plus qu’un joueur en vie 
                        • Toutes les civilisations vivantes au bout de 50 tours sont déclarées gagnantes 
                       
                        """);

        Text CombatTitre = new Text("Combats et interactions entre joueurs\n");
        CombatTitre.setStyle("-fx-font-weight: bold");
        Text CombatTexte = new Text(
                """
                        • Les joueurs peuvent attaquer les unités et bâtiments des autres joueurs.
                        • Les remparts sont toujours attaqués en premier, puis les unités puis vos bâtiments .Il ne faut donc pas négliger la défense de ses bâtiments.
                        • Le tchat est disponible pour discuter avec les autres joueurs afin de négocier des alliances.
                        
                  """);

        Text raceTitre = new Text("Choix de la race\n");
        raceTitre.setStyle("-fx-font-weight: bold");
        Text raceTexte = new Text(
                """
                        • Chaque joueur choisie une race au début de la partie.
                        • Chaque race a ses propres unités et bâtiments ce qui les rend uniques.
                        • Chaque race possède des bonus et malus qui influencent la stratégie de jeu du joueur.
                        • Les bâtiments et unités spéficiques à chaque race sont débloqués au fur et à mesure de la partie.
                        
                  """);

        Text ListeDesRaces = new Text("Liste des races avec leurs caractéristiques :\n");
        ListeDesRaces.setStyle("-fx-font-weight: bold");
        Text ListeDesRacesTexte = new Text(
                """
                        - Mort-Vivant : 
                          + Avantages : 30% de production d'or en plus
                          - Inconvénients : -25% de production d'intelligence, -25% de vie sur les unités
                        - Humain : 
                          + Avantages : 25% de production d'intelligence en plus
                          - Inconvénients : -25% de dégats sur les unités
                        - 0rc : 
                          + Avantages : 50% de dégats en plus, 25% de vie en plus
                          - Inconvénients : -50% de production d'intelligence, -25% de production d'or
                          (Recommendé pour les joueurs agressifs)
                        - Elfe :
                          + Avantages : +100% de production d'intelligence, 
                          - Inconvénients : -25% de vie sur les unités, -25% de dégats sur les unités
                        - Nain : 
                          + Avantages : 50% de production d'or en plus et 25% de vie en plus 
                          - Inconvénients : -25% de dégats sur les unités
                        - Nerlk :
                          + Avantages : 25% de dégats en plus, 25% de vie en plus
                          - Inconvénients :-50% de production d'or
                        - Primotaure :
                          + Avantages : 50% de production d'or en plus, 
                          - Inconvénients : -40% de degats sur les unités,
                        """
        );

        Text ListesBatimentsEtUnites = new Text("Liste des bâtiments :\n");
        ListesBatimentsEtUnites.setStyle("-fx-font-weight: bold");
        Text ListesBatimentsTexte = new Text(
                """
                        
                             ==========================
                             Bâtiments universels
                             ==========================
                                                
                            /**
                             * Carrière :
                             * - Structure permettant d'extraire des ressources naturelles.
                             * - Production d'or : FAIBLE
                             * - Chaque joueur en possède 2 au début de la partie.
                             */
                           \s
                            /**
                             * Mine :
                             * - Extraction de ressources à plus grande échelle.
                             * - Production d'or : FORTE
                             * - Chaque joueur en possède 0 au début.
                             */
                           \s
                            /**
                             * Caserne :
                             * - Permet de former plus d'unités de combat.
                             * - Production d'emplacements d'unités : FAIBLE
                             * - Chaque joueur en possède 1 au début.
                             */
                           \s
                            /**
                             * Bibliothèque :
                             * - Centre de savoir pour le développement des technologies.
                             * - Production d'intelligence : FAIBLE
                             * - Chaque joueur en possède 0 au début.
                             */
                           \s
                            /**
                             * Rempart :
                             * - Mur de protection contre les attaques ennemies.
                             * - Production de défense
                             * - Chaque joueur en possède 1 au début.
                             */
                                                
                             ==========================
                             Unité universelle
                             ==========================
                                                
                            /**
                             * Guerrier :
                             * - Unité de base polyvalente.
                             * - Prix : FAIBLE
                             * - Dégâts : FAIBLES
                             * - Vie : FAIBLE
                             */
                                                
                             ==========================
                             Race : Mort-Vivant
                             ==========================
                                                
                            /**
                             * Cimetière :
                             * - Permet de lever de nouvelles troupes.
                             * - Production d'unités : MOYENNE
                             */
                                                
                            /**
                             * Nécropole :
                             * - Ancienne ville magique, haut lieu de rituels.
                             * - Production d'intelligence : FORTE
                             * - Production d'unités : FORTE
                             */
                                                
                            /**
                             * Zombie :
                             * - Créature lente, mais dangereuse.
                             * - Prix : MOYEN
                             * - Dégâts : PUISSANTS
                             * - Vie : FAIBLE
                             */
                                                
                            /**
                             * Nécromancien :
                             * - Mage capable de lever les morts.
                             * - Prix : EXTRÊMEMENT ÉLEVÉ
                             * - Dégâts : EXTRÊMEMENT PUISSANTS
                             * - Vie : MOYENNE
                             */
                                                
                             ==========================
                             Race : Humain
                             ==========================
                                                
                            /**
                             * Église :
                             * - Lieu de foi et recrutement de troupes sacrées.
                             * - Production d'unités : FORTE
                             */
                                                
                            /**
                             * Château :
                             * - Centre de commandement, commerce et recherche.
                             * - Production d'or : FORTE
                             * - Production d'intelligence : FORTE
                             * - Production d'unités : TRÈS FAIBLE
                             */
                                                
                            /**
                             * Ingénieur de combat :
                             * - Expert en construction et réparation.
                             * - Prix : MOYEN
                             * - Dégâts : PUISSANTS
                             * - Vie : MOYENNE
                             */
                                                
                            /**
                             * Héros Légendaire :
                             * - Héros humain le plus puissant.
                             * - Prix : EXTRÊMEMENT ÉLEVÉ
                             * - Dégâts : EXTRÊMEMENT PUISSANTS
                             * - Vie : ÉLEVÉE
                             */
                                                
                             ==========================
                             Race : Orc
                             ==========================
                                                
                            /**
                             * Donjon :
                             * - Centre d'entraînement brutal.
                             * - Production d'unités : MOYENNE
                             */
                                                
                            /**
                             * Marché d'esclaves :
                             * - Commerce d'esclaves orc.
                             * - Production d'or : TRÈS FORTE
                             * - Production d'intelligence : FAIBLE
                             */
                                                
                            /**
                             * Uruk Noir :
                             * - Créature puissante mais peu intelligente.
                             * - Prix : ÉLEVÉ
                             * - Dégâts : MOYENS
                             * - Vie : ÉLEVÉE
                             */
                                                
                            /**
                             * Chef de guerre Orc :
                             * - Meneur redoutable.
                             * - Prix : EXTRÊMEMENT ÉLEVÉ
                             * - Dégâts : EXTRÊMEMENT PUISSANTS
                             * - Vie : TRÈS ÉLEVÉE
                             */
                                                
                             ==========================
                             Race : Elfe
                             ==========================
                                                
                            /**
                             * Arbre de Vie :
                             * - Source de vie et de sagesse.
                             * - Production d’intelligence : MOYENNE
                             * - Production d’or : TRÈS ÉLEVÉE
                             * - Production d’unités : MOYENNE
                             */
                                                
                            /**
                             * Tour de Mage :
                             * - Développement de sorts puissants.
                             * - Production d’or : ULTRA FAIBLE
                             * - Production d’intelligence : FAIBLE
                             * - Production d’unités : ÉLEVÉE
                             */
                                                
                            /**
                             * Mage Elfe :
                             * - Lanceur de sorts surpuissants.
                             * - Prix : TRÈS ÉLEVÉ
                             * - Dégâts : EXTRÊMEMENT PUISSANTS
                             * - Vie : TRÈS FAIBLE
                             */
                                                
                            /**
                             * Archer Elfe :
                             * - Maître du tir à distance.
                             * - Prix : EXTRÊMEMENT ÉLEVÉ
                             * - Dégâts : ULTRA PUISSANTS
                             * - Vie : MOYENNE
                             */
                                                
                             ==========================
                             Race : Nain
                             ==========================
                                                
                            /**
                             * Mine de Nain :
                             * - Source massive d’or.
                             * - Production d’or : TRÈS FORTE
                             */
                                                
                            /**
                             * Taverne de Nain :
                             * - Recrutement de troupes.
                             * - Production d’intelligence : FAIBLE
                             * - Production d’unités : TRÈS ÉLEVÉE
                             */
                                                
                            /**
                             * Roi Mineur :
                             * - Souverain nain.
                             * - Prix : MOYEN
                             * - Dégâts : PUISSANTS
                             * - Vie : FAIBLE
                             */
                                                
                            /**
                             * Nain Ultime :
                             * - Guerrier légendaire.
                             * - Prix : TRÈS ÉLEVÉ
                             * - Dégâts : TRÈS ÉLEVÉS
                             * - Vie : ÉLEVÉE
                             */
                                                
                             ==========================
                             Race : Nerlk
                             ==========================
                                                
                            /**
                             * Tente :
                             * - Formation des troupes.
                             * - Production d’unités : MOYENNE
                             */
                                                
                            /**
                             * Forge de Nerlk :
                             * - Création d’armes enchantées.
                             * - Production d’or : TRÈS ÉLEVÉE
                             * - Production d’intelligence : MOYENNE
                             */
                                                
                            /**
                             * Guerrier Nerlk :
                             * - Croisement Elfe-Orc, petit mais puissant.
                             * - Prix : MOYEN
                             * - Dégâts : PUISSANTS
                             * - Vie : FAIBLE
                             */
                                                
                            /**
                             * Berserker Nerlk :
                             * - Guerrier enragé.
                             * - Prix : TRÈS ÉLEVÉ
                             * - Dégâts : ULTRA PUISSANTS
                             * - Vie : MOYENNE
                             */
                                                
                             ==========================
                             Race : Primotaure
                             ==========================
                                                
                            /**
                             * Labyrinthe :
                             * - Bâtiment très résistant qui protège le royaume.
                             * - Production d’or : MOYENNE
                             * - Production d’intelligence : FAIBLE
                             * - Production d’unités : TRÈS FAIBLE
                             */
                                                
                            /**
                             * Temple :
                             * - Lieu de formation et de savoir.
                             * - Production d’or : ÉLEVÉE
                             * - Production d’intelligence : TRÈS FORTE
                             * - Production d’unités : TRÈS ÉLEVÉE
                             */
                                                
                            /**
                             * Minotaure :
                             * - Guerrier robuste et puissant.
                             * - Prix : MOYEN
                             * - Dégâts : PUISSANTS
                             * - Vie : MOYENNE
                             */
                                                
                            /**
                             * Dieu des Primotaures :
                             * - Être mythique, puissance divine.
                             * - Prix : ULTRA ÉLEVÉ
                             * - Dégâts : ULTRA PUISSANTS
                             * - Vie : ULTRA ÉLEVÉE
                             */
                        """
        );


        // Ajouter les morceaux de texte dans le TextFlow
        controller.textFlow.getChildren().addAll(
                mainTitle,
                intro1, intro2, intro3,
                objectifTitre, objectifTexte,
                toursTitre, toursTexte,
                finTitre, finTexte
                , raceTitre, raceTexte
                , ListeDesRaces, ListeDesRacesTexte
                , ListesBatimentsEtUnites, ListesBatimentsTexte
        );

        // Centrer le texte dans le ScrollPane si sa hauteur est inférieure à celle du ScrollPane
        InvalidationListener adaptTextPosition = (observable) -> {
            double viewportWidth = controller.textScrollPane.getViewportBounds().getWidth();
            if (controller.textFlow.getWidth() < viewportWidth) {
                controller.textFlow.setTranslateX((viewportWidth - controller.textFlow.getWidth()) / 2);
            } else {
                controller.textFlow.setTranslateX(0);
            }

            double viewportHeight = controller.textScrollPane.getViewportBounds().getHeight();
            if (controller.textFlow.getHeight() < viewportHeight) {
                controller.textFlow.setTranslateY((viewportHeight - controller.textFlow.getHeight()) / 2);
            } else {
                controller.textFlow.setTranslateY(0);
            }
        };

        controller.textScrollPane.viewportBoundsProperty().addListener(adaptTextPosition);
        controller.textFlow.widthProperty().addListener(adaptTextPosition);
        controller.textFlow.heightProperty().addListener(adaptTextPosition);
    }
}
