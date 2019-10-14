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

    public Cell() {
        state = EMPTY;
        grain = null;
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

    public void createNewGrain(){
        float r, g, b;
        Color color;

        do {
            r = ThreadLocalRandom.current().nextFloat();
            g = ThreadLocalRandom.current().nextFloat();
            b = ThreadLocalRandom.current().nextFloat();

            color = Color.color(r, g, b);
        }while (!isGrainColorAvailable(color));
        grain = new Grain(listOfGrains.size(), color);
        listOfGrains.add(grain);
        state = GRAIN;
    }

    public void turnToGrain(int ID, CellState state, Color color, boolean onBorder){
        this.state = state;
        this.onBorder = onBorder;
        Grain holdGrain = getGrainByID(ID);
        if (holdGrain == null) {
            grain = new Grain(ID, color);
            listOfGrains.add(grain);
        } else {
            grain = holdGrain;
        }
    }

    private boolean isGrainColorAvailable(Color color){
        if (Grain.restrictedColors.contains(color)){
            return false;
        }

        for (Grain g : listOfGrains){
            if (g.getColor().equals(color))
                return false;
        }
        return true;
    }

    private Grain getGrainByID(int ID){
        for (Grain g : listOfGrains){
            if (g.getID() == ID)
                return g;
        }
        return null;
    }
}
