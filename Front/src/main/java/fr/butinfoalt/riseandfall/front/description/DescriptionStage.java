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
                        • La partie se termine lorsqu’il ne reste plus qu’un nombre limité de civilisations en jeu (par exemple : 2 ou 3 joueurs survivants, selon le nombre initial).
                        • L’objectif est donc de faire partie des derniers survivants en éliminant ou surpassant ses adversaires.
                        • La stratégie de survie est aussi importante que l’agression ou la croissance.
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
                        * Bâtiment  universel pour toutes les races :
                
                          - Carrière :
                          Structure permettant d'extraire des ressources naturelles pour financer l'économie du royaume
                          + Production d'or faible
                          + Chaque joueur en possède 2 au début de la partie
                          
                        - Mine :
                            Structure permettant d'extraire des ressources naturelles pour financer l'économie du royaume
                            + Production d'or Forte
                            + Chaque joueur en possède 0 au début de la partie
                        
                        - Caserne :
                            Structure permettant de former des emplacements supplémentaires pour les unités de combat
                            + Production d'emplacement d'unités faible
                            + Chaque joueur en possède 1 au début de la partie
                       
                        - Bibliothèque :
                            Centre de savoir produisant de l'intelligence pour le développement des technologies
                            + Production d'intelligence faible
                            + Chaque joueur en possède 0 au début de la partie
                       * Unités universelles pour toutes les Races :
                        - Guerrier :
                            Une unité de combat faible et polyvalente
                            + Prix faible
                            + Dégâts faibles
                            + Vie faible
                            
                      ** Bâtiments et unités spécifiques à chaque Race **
                        * Mort-Vivant :
                            - Bâtiment : Cimetière
                              Lieu sacré des morts où les Mort-Vivants peuvent lever de nouvelles troupes
                              + Production d'unités moyenne
                            - Bâtiment : Nécropole
                              Ancienne ville de mage rempli de rituel sinistre
                              + Production d'intelligence forte
                              + Production d'intelligence moyenne
                              + Production d'unité Forte
                           
                            - Unité : Zombie
                              Une créature morte-vivante qui se déplace lentement, mais inflige des dégâts mortels
                              + Prix Moyen
                              + Dégâts puissants
                              + Vie faible
                            - Unité : Nécromancien
                                Un mage mort-vivant capable de lever des morts-vivants
                              + Prix extrêmement élevé
                              + Dégâts extrêmement puissants
                              + Vie moyenne
                        
                        * Humain :
                            - Bâtiment : Église
                              Édifice spirituel dédié aux Humains, offrant protection et recrutement d'unités pieuses
                              + Production d'unité forte
                            - Bâtiment : Château
                            Résidence royale des Humains, servant de centre de commandement, lieu de commerce et offrant une grande protection
                            Un lieu de savoir où les humains peuvent développer de nouvelles technologies
                              + production d'or forte
                              + production d'intelligence forte
                            + production d'unité très faible
                            
                            - Unité : Ingénieur de combat
                              Un combattant expert en ingénierie capable de construire et de réparer les infrastructures avec rapidité et efficacité
                              + Prix moyen
                              + Dégats puissants 
                              + Vie moyen
                            - Unité : Héros Légendaire
                              L'un des humains les plus puissant du monde
                              + Prix extrêmement élevé
                              + Dégâts extrêmement puissants
                              + Vie Elevée  
                              
                        * 0rc :
                            - Bâtiment : Donjon
                              Endroit qui respire la violence permettant de former des futur combattants
                              + Production d''unité moyenne
                            - Bâtiment : marché d'esclave
                              Endroit où les orcs achètent et vendent des esclaves
                              + Production d'or très forte
                              + Production d'intelligence faible
                            - Unité : Uruk Noir
                              Créature imposante et très puissante mais débile
                              + Prix elevé
                              + Dégâts moyens
                              + Vie élevée
                            - Unité : Chef de guerre 0rc
                              Un chef de guerre redoutable qui mène ses troupes à la victoire avec une force inégalée
                              + Prix extrêmement élevé
                              + Dégâts extrêmement puissants
                              + Vie très élevée
                        * Elfe :
                            - Bâtiment : Arbre de vie
                              Un arbre sacré qui produit de l'intelligence et des unités elfiques
                              + Production d'intelligence moyenne
                              + Production d'or très elevée
                                + Production d'unité moyenne
                            - Bâtiment : Tour de mage
                                Une tour magique qui permet aux elfes de développer des sorts puissants
                              + Production d'or ultra faible
                              + Production d'intelligence faible
                            + Production d'unité elevée
                            - Unité : Mage Elfe
                              Puissant mage
                              + Prix tres élevé
                              + Dégâts extrêmement puissants
                              + Vie très faible
                            - Unité : Archer Elfe
                              Archer légendaire poossèdant la plus forte attaque à distance du monde
                              + Prix extrêmement élevé
                              + Dégâts ultra puissants
                              + Vie moyenne
                        
                        
                            
                        
                          
                          
                          
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
