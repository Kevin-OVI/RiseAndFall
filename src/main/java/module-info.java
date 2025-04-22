module fr.butinfoalt1.riseandfall.front {
    requires javafx.controls;
    requires javafx.fxml;


    opens fr.butinfoalt1.riseandfall.front to javafx.fxml;
    exports fr.butinfoalt1.riseandfall.front;
    opens fr.butinfoalt1.riseandfall.front.description to javafx.fxml;
    exports fr.butinfoalt1.riseandfall.front.description;
    opens fr.butinfoalt1.riseandfall.front.components to javafx.fxml;
    exports fr.butinfoalt1.riseandfall.front.components;
}