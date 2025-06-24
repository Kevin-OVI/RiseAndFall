package fr.butinfoalt.riseandfall.front.game.orders;

import fr.butinfoalt.riseandfall.front.RiseAndFallApplication;
import fr.butinfoalt.riseandfall.front.View;
import fr.butinfoalt.riseandfall.front.ViewController;
import fr.butinfoalt.riseandfall.front.components.TitleLabel;
import fr.butinfoalt.riseandfall.front.gamelogic.CurrentClientPlayer;
import fr.butinfoalt.riseandfall.front.gamelogic.OtherClientPlayer;
import fr.butinfoalt.riseandfall.front.gamelogic.RiseAndFall;
import fr.butinfoalt.riseandfall.front.util.UIUtils;
import fr.butinfoalt.riseandfall.gamelogic.data.AttackPlayerOrderData;
import fr.butinfoalt.riseandfall.gamelogic.data.UnitType;
import fr.butinfoalt.riseandfall.network.packets.PacketUpdateOrders;
import fr.butinfoalt.riseandfall.util.ObjectIntMap;
import fr.butinfoalt.riseandfall.util.logging.LogManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Contrôleur pour la vue de la liste des attaques en cours.
 * Permet de visualiser, ajouter, modifier et supprimer des attaques.
 */
public class OrderAttacksListController implements ViewController {
    /**
     * La liste des attaques en cours, avec les nœuds correspondants.
     * Utilisée pour afficher et gérer les attaques dans la vue.
     */
    private final HashMap<AttackPlayerOrderData, Node> attackNodes = new HashMap<>();

    /**
     * La liste des attaques en attente de validation.
     * Elle est initialisée lors de l'affichage de la vue et mise à jour lors des modifications.
     */
    private ArrayList<AttackPlayerOrderData> pendingAttacks;

    /**
     * Champ pour l'image de fond de la vue.
     */
    @FXML
    public ImageView backgroundImageView;

    /**
     * Champ pour le composant racine de la vue.
     */
    public ScrollPane root;

    /**
     * Titre affiché lorsque aucune attaque n'est programmée.
     */
    @FXML
    public TitleLabel noAttacksScheduledTitle;

    /**
     * Titre affiché lorsque des attaques sont programmées.
     */
    @FXML
    public TitleLabel scheduledAttacksTitle;

    /**
     * Conteneur pour la liste des attaques, où chaque attaque est affichée.
     * Utilisé pour ajouter ou supprimer dynamiquement des attaques.
     */
    @FXML
    public VBox listContainer;

    /**
     * Bouton pour ajouter une nouvelle attaque.
     * Il est désactivé si le joueur a déjà le maximum d'attaques programmées ou s'il n'a pas d'unités disponibles.
     */
    @FXML
    public Button addAttackButton;

    /**
     * Champ pour afficher les messages d'erreur.
     */
    @FXML
    public Label errorMessage;

    /**
     * Méthode appelée lors de l'initialisation du contrôleur.
     * Elle configure l'image de fond de la scène.
     */
    @FXML
    public void initialize() {
        Scene scene = RiseAndFallApplication.getMainWindow().getScene();
        UIUtils.setBackgroundImage("images/background.png", scene, this.backgroundImageView, this.root);
    }

    /**
     * Méthode appelée lorsque la liste des attaques en attente change.
     * Elle met à jour l'affichage en fonction du nombre d'attaques programmées.
     */
    private void onPendingAttacksChange() {
        if (this.pendingAttacks.isEmpty()) {
            this.noAttacksScheduledTitle.setVisible(true);
            this.scheduledAttacksTitle.setVisible(false);
        } else {
            this.noAttacksScheduledTitle.setVisible(false);
            this.scheduledAttacksTitle.setVisible(true);
        }
        this.addAttackButton.setDisable(this.getAttackablePlayers().isEmpty() || this.getRemainingUnits().isEmpty());
    }

    /**
     * Méthode appelée lorsque la vue est affichée.
     * Elle initialise la liste des attaques en attente et affiche les attaques existantes.
     *
     * @param errorMessage Le message d'erreur à afficher, ou null si aucun message d'erreur n'est à afficher.
     */
    @Override
    public void onDisplayed(String errorMessage) {
        ViewController.super.onDisplayed(errorMessage);
        this.showError(errorMessage);

        if (this.pendingAttacks == null) {// Déjà chargée
            this.pendingAttacks = new ArrayList<>(RiseAndFall.getPlayer().getPendingAttacks());
        }

        this.onPendingAttacksChange();
        for (AttackPlayerOrderData attack : this.pendingAttacks) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/butinfoalt/riseandfall/front/components/attack-component.fxml"));
                Node attackNode = loader.load();

                OrderAttacksListItemController controller = loader.getController();
                controller.init(this, attack);

                this.listContainer.getChildren().add(attackNode);
                this.attackNodes.put(attack, attackNode);
            } catch (IOException e) {
                LogManager.logError("Erreur lors du chargement du composant de jeu :", e);
            }
        }
    }

    /**
     * Méthode appelée lorsque la vue est cachée.
     * Elle vide la liste des attaques et les nœuds associés pour libérer de la mémoire.
     */
    @Override
    public void onHidden() {
        ViewController.super.onHidden();

        this.listContainer.getChildren().clear();
        this.attackNodes.clear();
    }

    /**
     * Méthode appelée par JavaFX lorsque l'utilisateur clique sur le bouton pour ajouter une attaque.
     * Elle ouvre la vue de création d'une attaque.
     */
    @FXML
    public void handleAddAttack() {
        this.createOrEditAttack(null);
    }

    /**
     * Méthode appelée par JavaFX lorsque l'utilisateur clique sur le bouton pour sauvegarder les attaques.
     * Elle envoie les attaques en attente au serveur et retourne à la vue principale.
     */
    @FXML
    public void handleSave() {
        CurrentClientPlayer player = RiseAndFall.getPlayer();
        player.setPendingAttacks(this.pendingAttacks);
        try {
            RiseAndFall.getClient().sendPacket(new PacketUpdateOrders(null, null, player.getPendingAttacks()));
        } catch (IOException e) {
            this.showError("Erreur lors de l'envoi des ordres : " + e.getMessage());
            LogManager.logError("Erreur lors de l'envoi du paquet de mise à jour des ordres", e);
            return;
        }
        this.switchBack();
        this.showError(null);
    }

    /**
     * Méthode appelée par JavaFX lorsque l'utilisateur clique sur le bouton pour revenir à la vue principale.
     * Elle retire les attaques en attente pour ne pas les réutiliser si la vue est réaffichée
     * et retourne à la vue principale du jeu.
     */
    @FXML
    public void switchBack() {
        this.pendingAttacks = null;
        RiseAndFallApplication.switchToView(View.MAIN_RUNNING_GAME);
    }

    /**
     * Ouvre la vue de création ou d'édition d'une attaque.
     * Si l'attaque est null, une nouvelle attaque est créée.
     * Sinon, l'attaque existante est modifiée.
     *
     * @param attack L'attaque à créer ou modifier, ou null pour en créer une nouvelle.
     */
    public void createOrEditAttack(AttackPlayerOrderData attack) {
        RiseAndFallApplication.switchToView(View.ORDERS_ATTACK);
        OrderAttackController controller = View.ORDERS_ATTACK.getController();
        controller.init(this, attack);
    }

    /**
     * Supprime une attaque de la liste des attaques en attente.
     * Retire également le nœud associé à l'attaque de l'interface utilisateur.
     *
     * @param attack L'attaque à supprimer.
     */
    void removeAttack(AttackPlayerOrderData attack) {
        Node attackNode = this.attackNodes.remove(attack);
        if (attackNode != null) {
            this.listContainer.getChildren().remove(attackNode);
        }
        this.pendingAttacks.remove(attack);
        this.onPendingAttacksChange();
    }

    /**
     * Ajoute ou remplace une attaque dans la liste des attaques en attente.
     * Si l'attaque existe déjà, elle est remplacée par la nouvelle.
     * Sinon, elle est ajoutée à la liste.
     * La liste des attaques affichées n'est pas mise à jour immédiatement car on suppose que
     * la vue n'est pas affichée quand cette méthode est appelée.
     *
     * @param attack L'attaque à ajouter ou remplacer.
     * @param old    L'ancienne attaque à remplacer, ou null si c'est une nouvelle attaque.
     */
    void addOrReplaceAttack(AttackPlayerOrderData attack, AttackPlayerOrderData old) {
        if (old == null) {
            this.pendingAttacks.add(attack);
        } else {
            this.pendingAttacks.set(this.pendingAttacks.indexOf(old), attack);
        }
    }

    /**
     * Obtient la liste des joueurs qui n'ont pas encore été attaqués.
     * Exclut les joueurs déjà ciblés par une attaque en attente.
     *
     * @return Une liste de joueurs qui n'ont pas encore été attaqués.
     */
    List<OtherClientPlayer> getAttackablePlayers() {
        Set<OtherClientPlayer> alreadyAttackingPlayers = this.pendingAttacks.stream()
                .map(attackPlayerOrderData -> (OtherClientPlayer) attackPlayerOrderData.getTargetPlayer())
                .collect(Collectors.toSet());

        return RiseAndFall.getGame().getOtherPlayers().stream()
                .filter(otherClientPlayer -> !alreadyAttackingPlayers.contains(otherClientPlayer) && !otherClientPlayer.isEliminated())
                .toList();
    }

    /**
     * Obtient la liste des unités restantes disponibles pour les attaques.
     * Exclut les unités déjà utilisées dans les attaques en attente.
     *
     * @return Une map des types d'unités avec le nombre restant disponible.
     */
    ObjectIntMap<UnitType> getRemainingUnits() {
        CurrentClientPlayer player = RiseAndFall.getPlayer();
        ObjectIntMap<UnitType> remainingUnits = player.getUnitMap().clone();
        this.pendingAttacks.stream().map(AttackPlayerOrderData::getUsingUnits).forEach(remainingUnits::decrement);
        return remainingUnits;
    }

    /**
     * Méthode pour afficher un message d'erreur.
     *
     * @param error Le message d'erreur à afficher.
     */
    private void showError(String error) {
        if (error == null) {
            this.errorMessage.setVisible(false);
        } else {
            this.errorMessage.setText(error);
            this.errorMessage.setVisible(true);
        }
    }
}
