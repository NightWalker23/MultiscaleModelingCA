package model;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static model.CellState.*;
import static model.InclusionTypes.*;

public class Model {
    private int width, height;
    private Cell[][] grid;
    private List<Cell> listOfCells;
    private List<Cell> listOfAvailableCells;
    private List<Cell> listOfCellsOnBorder;

    private class Indexes {
        public int i, j, iG, iD, jL, jR;
    }

    private class MooreRules{
        public Grain grain;
        public int quantity;

        public MooreRules(Grain grain, int quantity) {
            this.grain = grain;
            this.quantity = quantity;
        }
    }

    public Model(int width, int height) {
        this.grid = new Cell[width][height];
        this.width = width;
        this.height = height;
        this.listOfCells = new ArrayList<>();
        this.listOfAvailableCells = new ArrayList<>();
        this.listOfCellsOnBorder = new ArrayList<>();

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

    public void setListOfAvailableCells(List<Cell> listOfAvailableCells) {
        this.listOfAvailableCells = listOfAvailableCells;
    }

    private void createEmptyGrid() {
        Cell holdCell;
        if (grid != null && listOfCells != null && listOfAvailableCells != null) {
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    holdCell = new Cell(new Coordinates(i, j));
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
                holdCell.createNewGrain(null);
                listOfAvailableCells.remove(holdCell);
                numberOfAvailableCells--;
            }
        }
    }

    public void startSimulation(int probabilityToChange) {
        while (listOfAvailableCells.size() != 0) {
            process(grid, probabilityToChange);
        }
    }

    public void process(Cell[][] frame, int probabilityToChange) {
        Cell[][] tmp = getTmp();
        Cell frameCell, tmpCell;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                frameCell = frame[i][j];
                tmpCell = tmp[i][j];
                if (frameCell.getState() == EMPTY) {
                    tmpCell.setGrain(determineState(frame, i, j, probabilityToChange));
                    if (tmpCell.getGrain() != null) {
                        tmpCell.setState(GRAIN);
                    }
                } else if (frameCell.getState() == GRAIN) {
                    tmpCell.setState(GRAIN);
                    tmpCell.setGrain(frame[i][j].getGrain());
                } else if (frameCell.getState() == INCLUSION) {
                    tmpCell.setState(INCLUSION);
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
                tmp[i][j] = new Cell(new Coordinates(i, j));

        return tmp;
    }

    private Grain determineState(Cell[][] frame, int height, int width, int probabilityToChange) {
        Indexes indexes = determineIndexes(height, width);

        return getNewState(frame, indexes, probabilityToChange);
    }

    private Indexes determineIndexes(int i, int j) {
        Indexes indexes = new Indexes();
        indexes.i = i;
        indexes.j = j;

        //periodic type of edge
        if (i == 0)
            indexes.iG = getWidth() - 1;
        else indexes.iG = i - 1;

        if (i == this.width - 1)
            indexes.iD = 0;
        else indexes.iD = i + 1;

        if (j == 0)
            indexes.jL = getHeight() - 1;
        else indexes.jL = j - 1;

        if (j == this.height - 1)
            indexes.jR = 0;
        else indexes.jR = j + 1;

        return indexes;
    }

    private Grain getNewState(Cell[][] frame, Indexes indexes, int probabilityToChange) {
        Map<Grain, Integer> grainMap = createNeighboursMapMoore(frame, indexes);
        MooreRules mr = getGrainMaxNeighbour(grainMap);
        int randomNumber;

        if (mr.quantity >= 5){
            return mr.grain;
        } else{
            grainMap = createNeighboursMapNearestMoore(frame, indexes);
            mr = getGrainMaxNeighbour(grainMap);
            if (mr.quantity >= 3){
                return mr.grain;
            } else {
                grainMap = createNeighboursMapFurtherMoore(frame, indexes);
                mr = getGrainMaxNeighbour(grainMap);
                if (mr.quantity >= 3){
                    return mr.grain;
                } else {
                    grainMap = createNeighboursMapMoore(frame, indexes);
                    mr = getGrainMaxNeighbour(grainMap);
                    randomNumber = ThreadLocalRandom.current().nextInt(1, 100);
                    if (randomNumber <= probabilityToChange){
                        return mr.grain;
                    }
                }
            }
        }

        return null;
    }

    private Map<Grain, Integer> createNeighboursMapMoore(Cell[][] frame, Indexes indexes) {
        Map<Grain, Integer> neighboursMap = new HashMap<>();
        Grain grain = null;

        if (indexes.iG != -1 && indexes.jL != -1)
            if (frame[indexes.iG][indexes.jL].getState() == GRAIN) {
                grain = frame[indexes.iG][indexes.jL].getGrain();
                if (grain != null) {
                    fillMap(grain, neighboursMap);
                }
            }

        if (indexes.iG != -1)
            if (frame[indexes.iG][indexes.j].getState() == GRAIN) {
                grain = frame[indexes.iG][indexes.j].getGrain();
                if (grain != null) {
                    fillMap(grain, neighboursMap);
                }
            }

        if (indexes.iG != -1 && indexes.jR != -1)
            if (frame[indexes.iG][indexes.jR].getState() == GRAIN) {
                grain = frame[indexes.iG][indexes.jR].getGrain();
                if (grain != null) {
                    fillMap(grain, neighboursMap);
                }
            }

        if (indexes.jL != -1)
            if (frame[indexes.i][indexes.jL].getState() == GRAIN) {
                grain = frame[indexes.i][indexes.jL].getGrain();
                if (grain != null) {
                    fillMap(grain, neighboursMap);
                }
            }

        if (indexes.jR != -1)
            if (frame[indexes.i][indexes.jR].getState() == GRAIN) {
                grain = frame[indexes.i][indexes.jR].getGrain();
                if (grain != null) {
                    fillMap(grain, neighboursMap);
                }
            }

        if (indexes.iD != -1 && indexes.jL != -1)
            if (frame[indexes.iD][indexes.jL].getState() == GRAIN) {
                grain = frame[indexes.iD][indexes.jL].getGrain();
                if (grain != null) {
                    fillMap(grain, neighboursMap);
                }
            }

        if (indexes.iD != -1)
            if (frame[indexes.iD][indexes.j].getState() == GRAIN) {
                grain = frame[indexes.iD][indexes.j].getGrain();
                if (grain != null) {
                    fillMap(grain, neighboursMap);
                }
            }

        if (indexes.iD != -1 && indexes.jR != -1)
            if (frame[indexes.iD][indexes.jR].getState() == GRAIN) {
                grain = frame[indexes.iD][indexes.jR].getGrain();
                if (grain != null) {
                    fillMap(grain, neighboursMap);
                }
            }

        return neighboursMap;
    }

    private Map<Grain, Integer> createNeighboursMapNearestMoore(Cell[][] frame, Indexes indexes) {
        Map<Grain, Integer> neighboursMap = new HashMap<>();
        Grain grain = null;

        if (indexes.iG != -1)
            if (frame[indexes.iG][indexes.j].getState() == GRAIN) {
                grain = frame[indexes.iG][indexes.j].getGrain();
                if (grain != null) {
                    fillMap(grain, neighboursMap);
                }
            }

        if (indexes.jL != -1)
            if (frame[indexes.i][indexes.jL].getState() == GRAIN) {
                grain = frame[indexes.i][indexes.jL].getGrain();
                if (grain != null) {
                    fillMap(grain, neighboursMap);
                }
            }

        if (indexes.jR != -1)
            if (frame[indexes.i][indexes.jR].getState() == GRAIN) {
                grain = frame[indexes.i][indexes.jR].getGrain();
                if (grain != null) {
                    fillMap(grain, neighboursMap);
                }
            }

        if (indexes.iD != -1)
            if (frame[indexes.iD][indexes.j].getState() == GRAIN) {
                grain = frame[indexes.iD][indexes.j].getGrain();
                if (grain != null) {
                    fillMap(grain, neighboursMap);
                }
            }

        return neighboursMap;
    }

    private Map<Grain, Integer> createNeighboursMapFurtherMoore(Cell[][] frame, Indexes indexes) {
        Map<Grain, Integer> neighboursMap = new HashMap<>();
        Grain grain = null;

        if (indexes.iG != -1 && indexes.jL != -1)
            if (frame[indexes.iG][indexes.jL].getState() == GRAIN) {
                grain = frame[indexes.iG][indexes.jL].getGrain();
                if (grain != null) {
                    fillMap(grain, neighboursMap);
                }
            }

        if (indexes.iG != -1 && indexes.jR != -1)
            if (frame[indexes.iG][indexes.jR].getState() == GRAIN) {
                grain = frame[indexes.iG][indexes.jR].getGrain();
                if (grain != null) {
                    fillMap(grain, neighboursMap);
                }
            }

        if (indexes.iD != -1 && indexes.jL != -1)
            if (frame[indexes.iD][indexes.jL].getState() == GRAIN) {
                grain = frame[indexes.iD][indexes.jL].getGrain();
                if (grain != null) {
                    fillMap(grain, neighboursMap);
                }
            }

        if (indexes.iD != -1 && indexes.jR != -1)
            if (frame[indexes.iD][indexes.jR].getState() == GRAIN) {
                grain = frame[indexes.iD][indexes.jR].getGrain();
                if (grain != null) {
                    fillMap(grain, neighboursMap);
                }
            }

        return neighboursMap;
    }

    private void fillMap(Grain grain, Map<Grain, Integer> grainMap) {
        if (grainMap.containsKey(grain)) grainMap.put(grain, grainMap.get(grain) + 1);
        else grainMap.put(grain, 1);
    }

    private MooreRules getGrainMaxNeighbour(Map<Grain, Integer> grainMap) {
        Map.Entry<Grain, Integer> maxEntry = null;
        int max = 0;

        for (Map.Entry<Grain, Integer> entry : grainMap.entrySet())
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0 && entry.getKey().getID() >= 0)
                maxEntry = entry;

        Grain grain = null;
        if (maxEntry != null) {
            max = maxEntry.getValue();

            List<Grain> listMax = new ArrayList<>();
            for (Map.Entry<Grain, Integer> entry : grainMap.entrySet())
                if (entry.getValue() == max)
                    listMax.add(entry.getKey());

            Random rand = new Random();
            int randWinner = rand.nextInt(listMax.size());
            grain = listMax.get(randWinner);
        }

        return new MooreRules(grain, max);
    }

    public void determineBorders() {
        listOfCellsOnBorder = new ArrayList<>();
        boolean onBorder;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                onBorder = false;
                Indexes indexes = determineIndexes(i, j);
                Map<Grain, Integer> grainMap = createNeighboursMapMoore(grid, indexes);
                for (Map.Entry<Grain, Integer> entry : grainMap.entrySet()) {
                    if (grid[i][j].getGrain() != null) {
                        if (entry.getKey().getID() != grid[i][j].getGrain().getID()) {
                            onBorder = true;
                            break;
                        }
                    }
                }
                grid[i][j].setOnBorder(onBorder);
                if (onBorder){
                    listOfCellsOnBorder.add(grid[i][j]);
                }
            }
        }
    }

    public void addInclusions(InclusionTypes iType, int amount, int radius){
        if (amount <= listOfCellsOnBorder.size() || listOfAvailableCells.size() != 0) {
            int randX, randY, randCell;
            Cell holdCell;
            for (int i = 0; i < amount; i++) {
                if (listOfAvailableCells.size() == 0) {
                    randCell = ThreadLocalRandom.current().nextInt(0, listOfCellsOnBorder.size());
                    holdCell = listOfCellsOnBorder.get(randCell);
                    createInclusion(holdCell.getCords().y, holdCell.getCords().x, radius, iType);
                    listOfCellsOnBorder.remove(holdCell);
                } else {
                    randX = ThreadLocalRandom.current().nextInt(0, height);
                    randY = ThreadLocalRandom.current().nextInt(0, width);
                    createInclusion(randX, randY, radius, iType);
                }
            }
        }
    }

    private Coordinates getUp(int x, int y, int range) {
        int cx = x, cy = y;

        for (int i = 0; i < range; i++) {
            if (cx == 0)
                cx = getHeight() - 1;
            else cx--;
        }

        return new Coordinates(cx, cy);
    }

    private Coordinates getDown(int x, int y, int range) {
        int cx = x, cy = y;

        for (int i = 0; i < range; i++) {
            if (cx == getHeight() - 1)
                cx = 0;
            else cx++;
        }

        return new Coordinates(cx, cy);
    }

    private Coordinates getLeft(int x, int y, int range) {
        int cx = x, cy = y;

        for (int i = 0; i < range; i++) {
            if (cy == 0)
                cy = getWidth() - 1;
            else cy--;
        }

        return new Coordinates(cx, cy);
    }

    private Coordinates getRight(int x, int y, int range) {
        int cx = x, cy = y;

        for (int i = 0; i < range; i++) {
            if (cy == getWidth() - 1)
                cy = 0;
            else cy++;
        }

        return new Coordinates(cx, cy);
    }

    private void createInclusion(int x, int y, int radius, InclusionTypes iType) {
        int circleX = 0, circleY = 0;

        Coordinates middleCell = new Coordinates(0, 0);

        Coordinates startCell = new Coordinates(x, y);

        if (iType == CIRCLE){
            radius -= 1;
            startCell = getLeft(startCell.x, startCell.y, radius);
            circleX -= radius;
            startCell = getUp(startCell.x, startCell.y, radius);
            circleY += radius;

            Coordinates tmpCell = new Coordinates(startCell.x, startCell.y);

            for (int i = 0; i < 2 * radius + 1; i++) {
                for (int j = 0; j < 2 * radius + 1; j++) {
                    if (getDistanceBetweenCells(middleCell, new Coordinates(circleX, circleY)) <= radius){
                        turnToInclusion(tmpCell);
                    }
                    tmpCell = getRight(tmpCell.x, tmpCell.y, 1);
                    circleX += 1;
                }
                tmpCell = getLeft(tmpCell.x, tmpCell.y, 2 * radius + 1);
                circleX -= 2 * radius + 1;
                tmpCell = getDown(tmpCell.x, tmpCell.y, 1);
                circleY -= 1;
            }
        } else if (iType == SQUARE){
            int a = (int) (Math.ceil(radius / Math.sqrt(2)));
            startCell = getLeft(startCell.x, startCell.y, a/2);
            circleX -= a/2;
            startCell = getUp(startCell.x, startCell.y, a/2);
            circleY += a/2;

            Coordinates tmpCell = new Coordinates(startCell.x, startCell.y);

            for (int i = 0; i < a; i++) {
                for (int j = 0; j < a; j++) {
                    turnToInclusion(tmpCell);
                    tmpCell = getRight(tmpCell.x, tmpCell.y, 1);
                    circleX += 1;
                }
                tmpCell = getLeft(tmpCell.x, tmpCell.y, a);
                circleX -= a;
                tmpCell = getDown(tmpCell.x, tmpCell.y, 1);
                circleY -= 1;
            }
        }
    }

    private void turnToInclusion(Coordinates tmpCell){
        grid[tmpCell.y][tmpCell.x].setState(INCLUSION);
        grid[tmpCell.y][tmpCell.x].setGrain(new Grain(-1, Grain.INCLUSION_COLOR));
        grid[tmpCell.y][tmpCell.x].setOnBorder(false);
        listOfAvailableCells.remove(grid[tmpCell.y][tmpCell.x]);
    }

    private double getDistanceBetweenCells(Coordinates startCell, Coordinates tmpCell){
        return (Math.sqrt( Math.pow(tmpCell.x - startCell.x, 2) + Math.pow(tmpCell.y - startCell.y, 2) ));
    }
}
