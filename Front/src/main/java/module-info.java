module fr.butinfoalt.riseandfall.front {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires fr.butinfoalt.riseandfall;


    opens fr.butinfoalt.riseandfall.front to javafx.fxml;
    exports fr.butinfoalt.riseandfall.front;
    opens fr.butinfoalt.riseandfall.front.description to javafx.fxml;
    exports fr.butinfoalt.riseandfall.front.description;
    opens fr.butinfoalt.riseandfall.front.components to javafx.fxml;
    exports fr.butinfoalt.riseandfall.front.components;
    opens fr.butinfoalt.riseandfall.front.gamelogic to javafx.fxml;
    exports fr.butinfoalt.riseandfall.front.gamelogic;
    exports fr.butinfoalt.riseandfall.front.orders;
    opens fr.butinfoalt.riseandfall.front.orders to javafx.fxml;
    exports fr.butinfoalt.riseandfall.front.orders.table;
    opens fr.butinfoalt.riseandfall.front.orders.table to javafx.fxml;
    exports fr.butinfoalt.riseandfall.front.orders.amountselector;
    opens fr.butinfoalt.riseandfall.front.orders.amountselector to javafx.fxml;
}
