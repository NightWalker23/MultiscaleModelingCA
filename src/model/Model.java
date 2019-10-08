package model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Model {
    private int width, height;
    private Cell[][] grid;
    private List<Cell> listOfCells;
    private List<Cell> listOfAvailableCells;

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
                holdCell.turnToGrain();
                listOfAvailableCells.remove(holdCell);
                numberOfAvailableCells--;
            }
        }
    }
}
