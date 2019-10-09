package model;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class Grain {
    private int ID;
    private Color color;
    public static Color INCLUSION_COLOR = Color.BLACK;
    public static Color MARTENSITE_COLOR = Color.RED;
    public static Color BORDER_COLOR = Color.BLACK;

    public static List<Color> restrictedColors = new ArrayList<>(){{
        add(INCLUSION_COLOR);   //inclusions color
        add(MARTENSITE_COLOR);     //martensite color
    }};

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
