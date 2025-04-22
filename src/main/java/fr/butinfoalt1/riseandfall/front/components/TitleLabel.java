package fr.butinfoalt1.riseandfall.front.components;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

public class TitleLabel extends Label {
    public TitleLabel() {
        this.init();
    }

    public TitleLabel(String s) {
        super(s);
        this.init();
    }

    public TitleLabel(String s, Node node) {
        super(s, node);
        this.init();
    }

    private void init() {
        Font currentFont = this.getFont();
        Font newFont = new Font(currentFont.getName(), currentFont.getSize() * 1.2);
        this.setFont(newFont);
    }
}
