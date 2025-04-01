module fr.butinfoalt1.riseandfall.front {
    requires javafx.controls;
    requires javafx.fxml;


    opens fr.butinfoalt1.riseandfall.front to javafx.fxml;
    exports fr.butinfoalt1.riseandfall.front;
}