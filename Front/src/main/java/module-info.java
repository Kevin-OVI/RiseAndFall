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
    opens fr.butinfoalt.riseandfall.front.authentification to javafx.fxml;
    exports fr.butinfoalt.riseandfall.front.authentification;
}
