module fr.butinfoalt.riseandfall.front {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires fr.butinfoalt.riseandfall;
    requires io.github.cdimascio.dotenv.java;
    requires java.sql; // Pas de SQL sur le client mais nécessaire pour l'objet java.sql.Timestamp


    opens fr.butinfoalt.riseandfall.front to javafx.fxml;
    exports fr.butinfoalt.riseandfall.front;
    opens fr.butinfoalt.riseandfall.front.description to javafx.fxml;
    exports fr.butinfoalt.riseandfall.front.description;
    opens fr.butinfoalt.riseandfall.front.components to javafx.fxml;
    exports fr.butinfoalt.riseandfall.front.components;
    opens fr.butinfoalt.riseandfall.front.gamelogic to javafx.fxml;
    exports fr.butinfoalt.riseandfall.front.gamelogic;
    exports fr.butinfoalt.riseandfall.front.game.orders;
    opens fr.butinfoalt.riseandfall.front.game.orders to javafx.fxml;
    exports fr.butinfoalt.riseandfall.front.game.orders.table;
    opens fr.butinfoalt.riseandfall.front.game.orders.table to javafx.fxml;
    exports fr.butinfoalt.riseandfall.front.game.orders.amountselector;
    opens fr.butinfoalt.riseandfall.front.game.orders.amountselector to javafx.fxml;
    opens fr.butinfoalt.riseandfall.front.authentification to javafx.fxml;
    exports fr.butinfoalt.riseandfall.front.authentification;
    opens fr.butinfoalt.riseandfall.front.game.gamelist to javafx.fxml;
    exports fr.butinfoalt.riseandfall.front.game.gamelist;
    exports fr.butinfoalt.riseandfall.front.game;
    opens fr.butinfoalt.riseandfall.front.game to javafx.fxml;
    exports fr.butinfoalt.riseandfall.front.game.logs;
    opens fr.butinfoalt.riseandfall.front.game.logs to javafx.fxml;
    exports fr.butinfoalt.riseandfall.front.chat;
    opens fr.butinfoalt.riseandfall.front.chat to javafx.fxml;

}
