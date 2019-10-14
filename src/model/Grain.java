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
    public static Color BACKGROUND_COLOR = Color.WHITE;

    public static List<Color> restrictedColors = new ArrayList<Color>() {{
        add(INCLUSION_COLOR);   //inclusions color
        add(MARTENSITE_COLOR);     //martensite color
        add(BACKGROUND_COLOR);
        add(BORDER_COLOR);
    }};

    private static List<Color> takenColors = new ArrayList<>();

    public Grain(int ID, Color color) {
        this.ID = ID;
        this.color = color;
        if (!takenColors.contains(color)) {
            takenColors.add(color);
        }
    }

    public int getID() {
        return ID;
    }

    public Color getColor() {
        return color;
    }

    public static List<Color> getTakenColors() {
        return takenColors;
    }

}
