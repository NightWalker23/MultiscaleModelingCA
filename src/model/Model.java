package model;

import javafx.scene.paint.Color;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static model.CellState.*;

public class Model {
    private int width, height;
    private Cell[][] grid;
    private List<Cell> listOfCells;
    private List<Cell> listOfAvailableCells;

    private class Indexes {
        public int i, j, iG, iD, jL, jR;
    }

    public Model(int width, int height) {
        this.grid = new Cell[width][height];
        this.width = width;
        this.height = height;
        this.listOfCells = new ArrayList<>();
        this.listOfAvailableCells = new ArrayList<>();

        createEmptyGrid();
    }

    public Cell[][] getGrid() {
        return grid;
    }

    public List<Cell> getListOfCells() {
        return listOfCells;
    }

    public List<Cell> getListOfAvailableCells() {
        return listOfAvailableCells;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    private void createEmptyGrid() {
        Cell holdCell;
        if (grid != null && listOfCells != null && listOfAvailableCells != null) {
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    holdCell = new Cell();
                    grid[i][j] = holdCell;
                    listOfCells.add(holdCell);
                    listOfAvailableCells.add(holdCell);
                }
            }
        }
    }

    public void fillGridWIthGrains(int numberOfGrainsToFill) {
        int numberOfAvailableCells = listOfAvailableCells.size();
        if (numberOfGrainsToFill <= numberOfAvailableCells) {
            int randomCell;
            for (int i = 0; i < numberOfGrainsToFill; i++) {
                randomCell = ThreadLocalRandom.current().nextInt(0, numberOfAvailableCells);
                Cell holdCell = listOfAvailableCells.get(randomCell);
                holdCell.createNewGrain();
                listOfAvailableCells.remove(holdCell);
                numberOfAvailableCells--;
            }
        }
    }

    public void startSimulation() {
        while (listOfAvailableCells.size() != 0) {
            process(grid);
        }
    }

    public void process(Cell[][] frame) {
        Cell[][] tmp = getTmp();
        Cell frameCell, tmpCell;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                frameCell = frame[i][j];
                tmpCell = tmp[i][j];
                if (frameCell.getState() == EMPTY) {
                    tmpCell.setGrain(determineState(frame, i, j));
                    if (tmpCell.getGrain() != null) {
                        tmpCell.setState(GRAIN);
                    }
                } else if (frameCell.getState() == GRAIN) {
                    tmpCell.setState(GRAIN);
                    tmpCell.setGrain(frame[i][j].getGrain());
                }
            }
        }

        for (int i = 0; i < width; i++)
            System.arraycopy(tmp[i], 0, grid[i], 0, height);

        listOfCells = new ArrayList<>();
        for (int i = 0; i < width; i++) {
            listOfCells.addAll(new ArrayList<Cell>(Arrays.asList(grid[i])));
        }

        listOfAvailableCells = new ArrayList<>();
        for (Cell cell : listOfCells) {
            if (cell.getState().equals(EMPTY)) {
                listOfAvailableCells.add(cell);
            }
        }
    }

    private Cell[][] getTmp() {
        Cell[][] tmp = new Cell[width][height];
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                tmp[i][j] = new Cell();

        return tmp;
    }

    private Grain determineState(Cell[][] frame, int height, int width) {
        Indexes indexes = determineIndexes(height, width);

        return moore(frame, indexes);
    }

    private Indexes determineIndexes(int i, int j) {
        Indexes indexes = new Indexes();
        indexes.i = i;
        indexes.j = j;

        if (i == 0)
            indexes.iG = -1;
        else indexes.iG = i - 1;

        if (i == this.width - 1)
            indexes.iD = -1;
        else indexes.iD = i + 1;

        if (j == 0)
            indexes.jL = -1;
        else indexes.jL = j - 1;

        if (j == this.height - 1)
            indexes.jR = -1;
        else indexes.jR = j + 1;

        return indexes;
    }

    private Grain moore(Cell[][] frame, Indexes indexes) {
        Map<Grain, Integer> grainMap = createNeighboursMap(frame, indexes);

        return getGrainMaxNeighbour(grainMap);
    }

    private Map<Grain, Integer> createNeighboursMap(Cell[][] frame, Indexes indexes) {
        Map<Grain, Integer> neighboursMap = new HashMap<>();
        Grain grain = null;

        if (indexes.iG != -1 && indexes.jL != -1)
            if (frame[indexes.iG][indexes.jL].getState() == GRAIN) {
                grain = frame[indexes.iG][indexes.jL].getGrain();
                fillMap(grain, neighboursMap);
            }

        if (indexes.iG != -1)
            if (frame[indexes.iG][indexes.j].getState() == GRAIN) {
                grain = frame[indexes.iG][indexes.j].getGrain();
                fillMap(grain, neighboursMap);
            }

        if (indexes.iG != -1 && indexes.jR != -1)
            if (frame[indexes.iG][indexes.jR].getState() == GRAIN) {
                grain = frame[indexes.iG][indexes.jR].getGrain();
                fillMap(grain, neighboursMap);
            }

        if (indexes.jL != -1)
            if (frame[indexes.i][indexes.jL].getState() == GRAIN) {
                grain = frame[indexes.i][indexes.jL].getGrain();
                fillMap(grain, neighboursMap);
            }

        if (indexes.jR != -1)
            if (frame[indexes.i][indexes.jR].getState() == GRAIN) {
                grain = frame[indexes.i][indexes.jR].getGrain();
                fillMap(grain, neighboursMap);
            }

        if (indexes.iD != -1 && indexes.jL != -1)
            if (frame[indexes.iD][indexes.jL].getState() == GRAIN) {
                grain = frame[indexes.iD][indexes.jL].getGrain();
                fillMap(grain, neighboursMap);
            }

        if (indexes.iD != -1)
            if (frame[indexes.iD][indexes.j].getState() == GRAIN) {
                grain = frame[indexes.iD][indexes.j].getGrain();
                fillMap(grain, neighboursMap);
            }

        if (indexes.iD != -1 && indexes.jR != -1)
            if (frame[indexes.iD][indexes.jR].getState() == GRAIN) {
                grain = frame[indexes.iD][indexes.jR].getGrain();
                fillMap(grain, neighboursMap);
            }

        return neighboursMap;
    }

    private void fillMap(Grain grain, Map<Grain, Integer> grainMap) {
        if (grainMap.containsKey(grain)) grainMap.put(grain, grainMap.get(grain) + 1);
        else grainMap.put(grain, 1);
    }

    private Grain getGrainMaxNeighbour(Map<Grain, Integer> grainMap) {
        Map.Entry<Grain, Integer> maxEntry = null;

        for (Map.Entry<Grain, Integer> entry : grainMap.entrySet())
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
                maxEntry = entry;

        Grain grain = null;
        if (maxEntry != null) {
            int max = maxEntry.getValue();

            List<Grain> listMax = new ArrayList<>();
            for (Map.Entry<Grain, Integer> entry : grainMap.entrySet())
                if (entry.getValue() == max)
                    listMax.add(entry.getKey());

            Random rand = new Random();
            int randWinner = rand.nextInt(listMax.size());
            grain = listMax.get(randWinner);
        }

        return grain;
    }

    public void determineBorders() {
        boolean onBorder;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                onBorder = false;
                Indexes indexes = determineIndexes(i, j);
                Map<Grain, Integer> grainMap = createNeighboursMap(grid, indexes);
                for (Map.Entry<Grain, Integer> entry : grainMap.entrySet()) {
                    if (entry.getKey().getID() != grid[i][j].getGrain().getID()) {
                        onBorder = true;
                        break;
                    }
                }
                grid[i][j].setOnBorder(onBorder);
            }
        }
    }
}
