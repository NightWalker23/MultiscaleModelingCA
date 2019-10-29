package model;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static model.CellState.*;

public class Cell {
    private static List<Grain> listOfGrains = new ArrayList<>();
    private Grain grain;
    private CellState state;
    private boolean onBorder;
    private Coordinates cords;

    public Cell(Coordinates cords) {
        state = EMPTY;
        grain = null;
        this.cords = cords;
    }

    public static List<Grain> getListOfGrains() {
        return listOfGrains;
    }

    public CellState getState() {
        return state;
    }

    public void setState(CellState state) {
        this.state = state;
    }

    public boolean isOnBorder() {
        return onBorder;
    }

    public void setOnBorder(boolean onBorder) {
        this.onBorder = onBorder;
    }

    public Grain getGrain() {
        return grain;
    }

    public void setGrain(Grain grain) {
        this.grain = grain;
    }

    public static void resetListOfGrains(){
        listOfGrains = new ArrayList<>();
        Grain.resetTakenColors();
    }

    public Coordinates getCords() {
        return cords;
    }

    public void createNewGrain(Color color) {
        /*
        TODO: dodać tą funkcję do wykonywania w nowym wątku
         */
        float r, g, b;
        Color randomColor;
        if (color == null) {
            do {
                r = ThreadLocalRandom.current().nextFloat();
                g = ThreadLocalRandom.current().nextFloat();
                b = ThreadLocalRandom.current().nextFloat();

                randomColor = Color.color(r, g, b);
            } while (!isGrainColorAvailable(randomColor));
            grain = new Grain(listOfGrains.size(), randomColor);
            listOfGrains.add(grain);
            state = GRAIN;
        } else {
            if (!Grain.getTakenColors().contains(color) && !Grain.restrictedColors.contains(color)) {
                grain = new Grain(listOfGrains.size(), color);
                listOfGrains.add(grain);
                Grain.getTakenColors().add(color);
                state = GRAIN;
            } else if (Grain.restrictedColors.contains(color)){
                if (color.equals(Grain.INCLUSION_COLOR)) {

                    for (Grain el : listOfGrains){
                        if (el.getID() == -1){
                            grain = el;
                            break;
                        }
                    }

                    if (grain == null) {
                        grain = new Grain(-1, Grain.INCLUSION_COLOR);
                        listOfGrains.add(grain);
                    }

                    state = INCLUSION;
                }
                else if (color.equals(Grain.DUAL_PHASE_COLOR)) {
                    for (Grain el : listOfGrains){
                        if (el.getID() == -2){
                            grain = el;
                            break;
                        }
                    }

                    if (grain == null) {
                        grain = new Grain(-2, Grain.DUAL_PHASE_COLOR);
                        listOfGrains.add(grain);
                    }

                    state = DP;
                }
            }
            else {
                grain = getGrainByColor(color);
                state = GRAIN;
            }
        }

//        if (state == EMPTY)
//            state = GRAIN;
    }

    public void turnToGrain(int ID, CellState state, Color color, boolean onBorder) {
        this.state = state;
        this.onBorder = onBorder;

        if (state == EMPTY){
            grain = null;
        } else {
            Grain holdGrain = getGrainByID(ID);
            if (holdGrain == null) {
                grain = new Grain(ID, color);
                listOfGrains.add(grain);
            } else {
                grain = holdGrain;
            }
        }
    }

    private boolean isGrainColorAvailable(Color color) {
        if (Grain.restrictedColors.contains(color)) {
            return false;
        }

        for (Grain g : listOfGrains) {
            if (g.getColor().equals(color))
                return false;
        }
        return true;
    }

    private Grain getGrainByID(int ID) {
        for (Grain g : listOfGrains) {
            if (g.getID() == ID)
                return g;
        }
        return null;
    }

    private Grain getGrainByColor(Color color) {
        for (Grain g : listOfGrains) {
            if (g.getColor().equals(color))
                return g;
        }
        return new Grain(-1, Grain.INCLUSION_COLOR);// null;
    }
}
