package model;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class Grain {
    private int ID;
    public static int INCLUSION_ID = -1;
    public static int DUAL_PHASE_ID = -2;
    private boolean frozen;
    public static int iteratorID = 0;

    private Color color;
    public static Color INCLUSION_COLOR = Color.BLACK;
    public static Color DUAL_PHASE_COLOR = Color.RED;
    public static Color BORDER_COLOR = Color.BLACK;
    public static Color BACKGROUND_COLOR = Color.WHITE;

    public static List<Color> restrictedColors = new ArrayList<Color>() {{
        add(INCLUSION_COLOR);   //inclusions color
        add(DUAL_PHASE_COLOR);     //dual phase color
        add(BACKGROUND_COLOR);
        add(BORDER_COLOR);
    }};

    private static List<Color> takenColors = new ArrayList<>();

    public static void setTakenColors(List<Color> takenColors) {
        Grain.takenColors = takenColors;
    }

    public static void resetTakenColors() {
        Grain.takenColors = new ArrayList<>();
    }


    public Grain(int ID, Color color) {
        this.ID = ID;
        this.color = color;
        if (!takenColors.contains(color)) {
            takenColors.add(color);
        }
        frozen = false;
        iteratorID++;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public static List<Color> getTakenColors() {
        return takenColors;
    }

}
