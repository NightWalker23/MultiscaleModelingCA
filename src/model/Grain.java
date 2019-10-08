package model;

import javafx.scene.paint.Color;

public class Grain {
    private int ID;
    private Color color;

    public Grain(int ID, Color color) {
        this.ID = ID;
        this.color = color;
    }

    public int getID() {
        return ID;
    }

    public Color getColor() {
        return color;
    }
}
