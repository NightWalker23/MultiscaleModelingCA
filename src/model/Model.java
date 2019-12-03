package model;

import javafx.scene.paint.Color;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static model.CellState.*;
import static model.InclusionTypes.*;
import static model.StructureTypes.*;

public class Model {
    private int width, height;
    private boolean changed, changedRandom;
    private Cell[][] grid;
    private List<Cell> listOfCells;
    private List<Cell> listOfCellsMC;
    private List<Cell> listOfAvailableCells;
    private List<Cell> listOfCellsOnBorder;
    private List<Grain> listOfSelectedGrainsSubstructure;
    private List<Grain> listOfSelectedGrainsDualPhase;
    private List<Grain> listOfSelectedGrainsGB;

    private class Indexes {
        public int i, j, iG, iD, jL, jR;
    }

    private class MooreRules {
        public Grain grain;
        public int quantity;

        public MooreRules(Grain grain, int quantity) {
            this.grain = grain;
            this.quantity = quantity;
        }
    }

    public Model(int width, int height) {
        this.grid = new Cell[width][height];
        Cell.resetListOfGrains();
        this.width = width;
        this.height = height;
        this.listOfCells = new ArrayList<>();
        this.listOfAvailableCells = new ArrayList<>();
        this.listOfCellsOnBorder = new ArrayList<>();
        this.listOfCellsMC = new ArrayList<>();
        this.listOfSelectedGrainsSubstructure = new ArrayList<>();
        this.listOfSelectedGrainsDualPhase = new ArrayList<>();
        this.listOfSelectedGrainsGB = new ArrayList<>();
        changed = false;
        changedRandom = false;

        createEmptyGrid();
    }

    public boolean isSimulationFinished() {
//        return (listOfAvailableCells.size() != 0 && (!changed || !changedRandom));
        return (listOfAvailableCells.size() == 0 || (!changed && !changedRandom));
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public boolean isChangedRandom() {
        return changedRandom;
    }

    public void setChangedRandom(boolean changedRandom) {
        this.changedRandom = changedRandom;
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

    public List<Grain> getListOfSelectedGrainsSubstructure() {
        return listOfSelectedGrainsSubstructure;
    }

    public void setListOfSelectedGrainsSubstructure(List<Grain> listOfSelectedGrainsSubstructure) {
        this.listOfSelectedGrainsSubstructure = listOfSelectedGrainsSubstructure;
    }

    public void addElementToListOfSelectedGrainsSubstructure(Grain element) {
        if (listOfSelectedGrainsSubstructure != null) {
            listOfSelectedGrainsSubstructure.add(element);
        }
    }

    public List<Grain> getListOfSelectedGrainsDualPhase() {
        return listOfSelectedGrainsDualPhase;
    }

    public void setListOfSelectedGrainsDualPhase(List<Grain> listOfSelectedGrainsDualPhase) {
        this.listOfSelectedGrainsDualPhase = listOfSelectedGrainsDualPhase;
    }

    public void addElementToListOfSelectedGrainsDualPhase(Grain element) {
        if (listOfSelectedGrainsDualPhase != null) {
            listOfSelectedGrainsDualPhase.add(element);
        }
    }

    public List<Grain> getListOfSelectedGrainsGB() {
        return listOfSelectedGrainsGB;
    }

    public void setListOfSelectedGrainsGB(List<Grain> listOfSelectedGrainsGB) {
        this.listOfSelectedGrainsGB = listOfSelectedGrainsGB;
    }

    public void addElementToListOfSelectedGrainsGB(Grain element) {
        if (listOfSelectedGrainsGB != null) {
            listOfSelectedGrainsGB.add(element);
        }
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

    public void fillGridWIthGrainsMC(int numberOfGrainsToFill) {
        Cell.resetListOfGrains();
        int numberOfAvailableCells = listOfAvailableCells.size();
        if (numberOfGrainsToFill <= numberOfAvailableCells && numberOfGrainsToFill > 0) {
            for (int i = 1; i <= numberOfGrainsToFill; i++) {
                float r, g, b;
                Color randomColor;
                do {
                    r = ThreadLocalRandom.current().nextFloat();
                    g = ThreadLocalRandom.current().nextFloat();
                    b = ThreadLocalRandom.current().nextFloat();

                    randomColor = Color.color(r, g, b);
                } while (!Cell.isGrainColorAvailable(randomColor));
                Cell.getListOfGrains().add(new Grain((Grain.iteratorID + 1), randomColor));
            }

            int randomGrain;
            if (numberOfAvailableCells == listOfCells.size()) {
                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        randomGrain = ThreadLocalRandom.current().nextInt(0, numberOfGrainsToFill);
                        Cell holdCell = grid[i][j];
                        holdCell.setGrain(Cell.getListOfGrains().get(randomGrain));
                        holdCell.setState(GRAIN);
                        listOfAvailableCells.remove(holdCell);
                        numberOfAvailableCells--;
                    }
                }
            } else {
                for (Cell el : listOfAvailableCells) {
                    randomGrain = ThreadLocalRandom.current().nextInt(0, numberOfGrainsToFill);
                    el.setGrain(Cell.getListOfGrains().get(randomGrain));
                    el.setState(GRAIN);
                    numberOfAvailableCells--;
                }
                listOfAvailableCells.removeAll(listOfAvailableCells);
            }
        }
    }

    public void startSimulation(int probabilityToChange, boolean type) {
        do {
            process(grid, probabilityToChange, type);
        } while (listOfAvailableCells.size() != 0 && (changed || changedRandom));

    }

    public void startSimulationMC(int iterations, double J) {
        listOfCellsMC.clear();
        for (Cell el : listOfCells)
            if (!el.getGrain().isFrozen()) listOfCellsMC.add(el);

        for (int i = 0; i < iterations; i++){
            processMC(J);
        }

        determineBorders();
    }

    public void process(Cell[][] frame, int probabilityToChange, boolean type) {
        Cell[][] tmp = getTmp();
        Cell frameCell, tmpCell;
        changed = false;
        changedRandom = false;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {

                frameCell = frame[i][j];
                tmpCell = tmp[i][j];
                if (frameCell.getState() == EMPTY) {
                    tmpCell.setGrain(determineState(frame, i, j, probabilityToChange, type));
                    if (tmpCell.getGrain() != null) {
                        tmpCell.setState(GRAIN);
                        changed = true;
                    }
                } else if (frameCell.getState() == GRAIN) {
                    tmpCell.setState(GRAIN);
                    tmpCell.setGrain(frame[i][j].getGrain());
                } else if (frameCell.getState() == INCLUSION) {
                    tmpCell.setState(INCLUSION);
                    tmpCell.setGrain(frame[i][j].getGrain());
                } else if (frameCell.getState() == DP) {
                    tmpCell.setState(DP);
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


    public void processMC(double J) {
        Collections.shuffle(listOfCellsMC);
        for (Cell el : listOfCellsMC){
            el.setGrain(determineStateMC(el, J));
        }
    }


    private Cell[][] getTmp() {
        Cell[][] tmp = new Cell[width][height];
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                tmp[i][j] = new Cell(new Coordinates(i, j));

        return tmp;
    }

    private Grain determineState(Cell[][] frame, int height, int width, int probabilityToChange, boolean type) {
        Indexes indexes = determineIndexes(height, width);

        if (type)
            return getNewState(frame, indexes, probabilityToChange);
        else
            return getClassicNewState(frame, indexes);
    }


    private Grain determineStateMC(Cell tmpCell, double J) {
        Indexes indexes = determineIndexes(tmpCell.getCords().x, tmpCell.getCords().y);

        return getNewStateMC(tmpCell, indexes, J);
    }

    private Grain getNewStateMC(Cell tmpCell, Indexes indexes, double J) {
        Map<Grain, Integer> grainMap = createNeighboursMapMoore(grid, indexes);
        Grain tmpGrain = null;
        double currentEenrgy = calculateEnergy(grainMap, tmpCell.getGrain(), J);

        Object[] values = grainMap.keySet().toArray();
        if (values.length == 0) return tmpCell.getGrain();
        Object randomValue = (Grain)values[ThreadLocalRandom.current().nextInt(0, values.length)];
        tmpGrain = (Grain)randomValue;

        double newEnergy = calculateEnergy(grainMap, tmpGrain, J);
        double deltaE = newEnergy - currentEenrgy;

        return deltaE <= 0 ? tmpGrain : tmpCell.getGrain();
    }


    private double calculateEnergy(Map<Grain, Integer> grainMap, Grain tmpGrain, double J){
        int energy = 0;

        for (Map.Entry<Grain, Integer> entry : grainMap.entrySet()){
            if (entry.getKey() != tmpGrain){
                energy += entry.getValue();
            }
        }

        return J * energy;
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

    private Grain getClassicNewState(Cell[][] frame, Indexes indexes) {
        Map<Grain, Integer> grainMap = createNeighboursMapMoore(frame, indexes);
        MooreRules mr = getGrainMaxNeighbour(grainMap);

        return mr.grain;
    }

    private Grain getNewState(Cell[][] frame, Indexes indexes, int probabilityToChange) {
        Map<Grain, Integer> grainMap = createNeighboursMapMoore(frame, indexes);
        MooreRules mr = getGrainMaxNeighbour(grainMap);
        int randomNumber;

        if (mr.quantity >= 5) {
            return mr.grain;
        } else {
            grainMap = createNeighboursMapNearestMoore(frame, indexes);
            mr = getGrainMaxNeighbour(grainMap);
            if (mr.quantity >= 3) {
                return mr.grain;
            } else {
                grainMap = createNeighboursMapFurtherMoore(frame, indexes);
                mr = getGrainMaxNeighbour(grainMap);
                if (mr.quantity >= 3) {
                    return mr.grain;
                } else {
                    grainMap = createNeighboursMapMoore(frame, indexes);
                    mr = getGrainMaxNeighbour(grainMap);
                    if (mr.quantity > 0) {
                        randomNumber = ThreadLocalRandom.current().nextInt(1, 100);
                        if (randomNumber <= probabilityToChange) {
                            return mr.grain;
                        } else {
                            changedRandom = true;
                        }
                    }
                }
            }
        }

        return null;
    }

    private Map<Grain, Integer> createNeighboursMapMoore(Cell[][] frame, Indexes indexes) {
        Map<Grain, Integer> neighboursMap = new HashMap<>();
        Grain grain = null;
        Cell holdCell = null;

        if (indexes.iG != -1 && indexes.jL != -1) {
            holdCell = frame[indexes.iG][indexes.jL];
            if (holdCell.getState() == GRAIN || holdCell.getState() == DP) {
                grain = frame[indexes.iG][indexes.jL].getGrain();
                if (grain != null) {
                    fillMap(grain, neighboursMap);
                }
            }
        }

        if (indexes.iG != -1) {
            holdCell = frame[indexes.iG][indexes.j];
            if (holdCell.getState() == GRAIN || holdCell.getState() == DP) {
                grain = frame[indexes.iG][indexes.j].getGrain();
                if (grain != null) {
                    fillMap(grain, neighboursMap);
                }
            }
        }

        if (indexes.iG != -1 && indexes.jR != -1) {
            holdCell = frame[indexes.iG][indexes.jR];
            if (holdCell.getState() == GRAIN || holdCell.getState() == DP) {
                grain = frame[indexes.iG][indexes.jR].getGrain();
                if (grain != null) {
                    fillMap(grain, neighboursMap);
                }
            }
        }

        if (indexes.jL != -1) {
            holdCell = frame[indexes.i][indexes.jL];
            if (holdCell.getState() == GRAIN || holdCell.getState() == DP) {
                grain = frame[indexes.i][indexes.jL].getGrain();
                if (grain != null) {
                    fillMap(grain, neighboursMap);
                }
            }
        }

        if (indexes.jR != -1) {
            holdCell = frame[indexes.i][indexes.jR];
            if (holdCell.getState() == GRAIN || holdCell.getState() == DP) {
                grain = frame[indexes.i][indexes.jR].getGrain();
                if (grain != null) {
                    fillMap(grain, neighboursMap);
                }
            }
        }

        if (indexes.iD != -1 && indexes.jL != -1) {
            holdCell = frame[indexes.iD][indexes.jL];
            if (holdCell.getState() == GRAIN || holdCell.getState() == DP) {
                grain = frame[indexes.iD][indexes.jL].getGrain();
                if (grain != null) {
                    fillMap(grain, neighboursMap);
                }
            }
        }

        if (indexes.iD != -1) {
            holdCell = frame[indexes.iD][indexes.j];
            if (holdCell.getState() == GRAIN || holdCell.getState() == DP) {
                grain = frame[indexes.iD][indexes.j].getGrain();
                if (grain != null) {
                    fillMap(grain, neighboursMap);
                }
            }
        }

        if (indexes.iD != -1 && indexes.jR != -1) {
            holdCell = frame[indexes.iD][indexes.jR];
            if (holdCell.getState() == GRAIN || holdCell.getState() == DP) {
                grain = frame[indexes.iD][indexes.jR].getGrain();
                if (grain != null) {
                    fillMap(grain, neighboursMap);
                }
            }
        }

        return neighboursMap;
    }

    private Map<Grain, Integer> createNeighboursMapNearestMoore(Cell[][] frame, Indexes indexes) {
        Map<Grain, Integer> neighboursMap = new HashMap<>();
        Grain grain = null;
        Cell holdCell = null;

        if (indexes.iG != -1) {
            holdCell = frame[indexes.iG][indexes.j];
            if (holdCell.getState() == GRAIN) {
                grain = frame[indexes.iG][indexes.j].getGrain();
                if (grain != null) {
                    fillMap(grain, neighboursMap);
                }
            }
        }

        if (indexes.jL != -1) {
            holdCell = frame[indexes.i][indexes.jL];
            if (holdCell.getState() == GRAIN) {
                grain = frame[indexes.i][indexes.jL].getGrain();
                if (grain != null) {
                    fillMap(grain, neighboursMap);
                }
            }
        }

        if (indexes.jR != -1) {
            holdCell = frame[indexes.i][indexes.jR];
            if (holdCell.getState() == GRAIN) {
                grain = frame[indexes.i][indexes.jR].getGrain();
                if (grain != null) {
                    fillMap(grain, neighboursMap);
                }
            }
        }

        if (indexes.iD != -1) {
            holdCell = frame[indexes.iD][indexes.j];
            if (holdCell.getState() == GRAIN) {
                grain = frame[indexes.iD][indexes.j].getGrain();
                if (grain != null) {
                    fillMap(grain, neighboursMap);
                }
            }
        }

        return neighboursMap;
    }

    private Map<Grain, Integer> createNeighboursMapFurtherMoore(Cell[][] frame, Indexes indexes) {
        Map<Grain, Integer> neighboursMap = new HashMap<>();
        Grain grain = null;
        Cell holdCell = null;

        if (indexes.iG != -1 && indexes.jL != -1) {
            holdCell = frame[indexes.iG][indexes.jL];
            if (holdCell.getState() == GRAIN) {
                grain = frame[indexes.iG][indexes.jL].getGrain();
                if (grain != null) {
                    fillMap(grain, neighboursMap);
                }
            }
        }

        if (indexes.iG != -1 && indexes.jR != -1) {
            holdCell = frame[indexes.iG][indexes.jR];
            if (holdCell.getState() == GRAIN) {
                grain = frame[indexes.iG][indexes.jR].getGrain();
                if (grain != null) {
                    fillMap(grain, neighboursMap);
                }
            }
        }

        if (indexes.iD != -1 && indexes.jL != -1) {
            holdCell = frame[indexes.iD][indexes.jL];
            if (holdCell.getState() == GRAIN) {
                grain = frame[indexes.iD][indexes.jL].getGrain();
                if (grain != null) {
                    fillMap(grain, neighboursMap);
                }
            }
        }

        if (indexes.iD != -1 && indexes.jR != -1) {
            holdCell = frame[indexes.iD][indexes.jR];
            if (holdCell.getState() == GRAIN) {
                grain = frame[indexes.iD][indexes.jR].getGrain();
                if (grain != null) {
                    fillMap(grain, neighboursMap);
                }
            }
        }

        return neighboursMap;
    }

    private void fillMap(Grain grain, Map<Grain, Integer> grainMap) {
        if (grain.isFrozen())
            return;

        if (grainMap.containsKey(grain)) grainMap.put(grain, grainMap.get(grain) + 1);
        else grainMap.put(grain, 1);
    }

    private MooreRules getGrainMaxNeighbour(Map<Grain, Integer> grainMap) {
        Map.Entry<Grain, Integer> maxEntry = null;
        int max = 0;

        for (Map.Entry<Grain, Integer> entry : grainMap.entrySet())
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0 && entry.getKey().getID() >= 0 && !entry.getKey().isFrozen())
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
                if (onBorder) {
                    listOfCellsOnBorder.add(grid[i][j]);
                }
            }
        }
    }

    public void addInclusions(InclusionTypes iType, int amount, int radius) {
        if (amount <= listOfCellsOnBorder.size() || listOfAvailableCells.size() != 0) {
            int randX, randY, randCell;
            Cell holdCell;
            for (int i = 0; i < amount; i++) {
                if (listOfCellsOnBorder.size() > 0) {
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

        if (iType == CIRCLE) {
            radius -= 1;
            startCell = getLeft(startCell.x, startCell.y, radius);
            circleX -= radius;
            startCell = getUp(startCell.x, startCell.y, radius);
            circleY += radius;

            Coordinates tmpCell = new Coordinates(startCell.x, startCell.y);

            for (int i = 0; i < 2 * radius + 1; i++) {
                for (int j = 0; j < 2 * radius + 1; j++) {
                    if (getDistanceBetweenCells(middleCell, new Coordinates(circleX, circleY)) <= radius) {
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
        } else if (iType == SQUARE) {
            int a = (int) (Math.ceil(radius / Math.sqrt(2)));
            startCell = getLeft(startCell.x, startCell.y, a / 2);
            circleX -= a / 2;
            startCell = getUp(startCell.x, startCell.y, a / 2);
            circleY += a / 2;

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

    private void turnToInclusion(Coordinates tmpCell) {
        grid[tmpCell.y][tmpCell.x].setState(INCLUSION);
        grid[tmpCell.y][tmpCell.x].setGrain(new Grain(Grain.INCLUSION_ID, Grain.INCLUSION_COLOR));
        grid[tmpCell.y][tmpCell.x].setOnBorder(false);
        listOfAvailableCells.remove(grid[tmpCell.y][tmpCell.x]);
    }

    private double getDistanceBetweenCells(Coordinates startCell, Coordinates tmpCell) {
        return (Math.sqrt(Math.pow(tmpCell.x - startCell.x, 2) + Math.pow(tmpCell.y - startCell.y, 2)));
    }

    public void addRemoveSelectedGrain(Grain selectedGrain, StructureTypes structureTypes) {
        if (structureTypes.equals(SUBSTRUCTURE)) {
            if (selectedGrain != null && listOfSelectedGrainsSubstructure != null) {
                if (!listOfSelectedGrainsDualPhase.contains(selectedGrain)) {
                    if (listOfSelectedGrainsSubstructure.contains(selectedGrain)) {

                        listOfSelectedGrainsSubstructure.remove(selectedGrain);
                        selectedGrain.setFrozen(false);

                        listOfSelectedGrainsGB.remove(selectedGrain);
                        for (Grain el : Cell.getListOfGrains()) {
                            if (el != selectedGrain)
                                if (el.getID() == selectedGrain.getID()) {
                                    listOfSelectedGrainsSubstructure.remove(el);
                                    listOfSelectedGrainsGB.remove(el);
                                }
                        }
                    } else {
                        listOfSelectedGrainsSubstructure.add(selectedGrain);
                        listOfSelectedGrainsGB.add(selectedGrain);
                        selectedGrain.setFrozen(true);
                        for (Grain el : Cell.getListOfGrains()) {
                            if (el != selectedGrain)
                                if (el.getID() == selectedGrain.getID()) {
                                    listOfSelectedGrainsSubstructure.add(el);
                                    listOfSelectedGrainsGB.add(el);
                                }
                        }

                    }
                }
            }
        } else if (structureTypes.equals(DUAL_PHASE)) {
            if (selectedGrain != null && listOfSelectedGrainsDualPhase != null) {
                if (!listOfSelectedGrainsSubstructure.contains(selectedGrain)) {
                    if (listOfSelectedGrainsDualPhase.contains(selectedGrain)) {

                        listOfSelectedGrainsDualPhase.remove(selectedGrain);
                        listOfSelectedGrainsGB.remove(selectedGrain);
                        selectedGrain.setFrozen(false);
                        for (Grain el : Cell.getListOfGrains()) {
                            if (el != selectedGrain)
                                if (el.getID() == selectedGrain.getID()) {
                                    listOfSelectedGrainsDualPhase.remove(el);
                                    listOfSelectedGrainsGB.remove(el);
                                }
                        }

                    } else {

                        listOfSelectedGrainsDualPhase.add(selectedGrain);
                        listOfSelectedGrainsGB.add(selectedGrain);
                        selectedGrain.setFrozen(true);
                        for (Grain el : Cell.getListOfGrains()) {
                            if (el != selectedGrain)
                                if (el.getID() == selectedGrain.getID()) {
                                    listOfSelectedGrainsDualPhase.add(el);
                                    listOfSelectedGrainsGB.add(el);
                                }
                        }
                    }
                }
            }
        }
    }

    public void startStructure(boolean type, int probabilityToChange, int numberOfGrains) {

        for (Grain el : listOfSelectedGrainsSubstructure) {
            el.setFrozen(true);
        }

        for (Grain el : listOfSelectedGrainsDualPhase) {
            el.setFrozen(true);
            el.setColor(Grain.DUAL_PHASE_COLOR);
            el.setID(Grain.DUAL_PHASE_ID);

            for (Cell el_c : listOfCells) {
                if (listOfSelectedGrainsDualPhase.contains(el_c.getGrain())) {
                    el_c.setState(DP);
                }
            }
        }

        for (Cell el : listOfCells) {
            if (!listOfSelectedGrainsSubstructure.contains(el.getGrain()) && !listOfSelectedGrainsDualPhase.contains(el.getGrain())) {
                el.setState(EMPTY);
                el.setOnBorder(false);
                el.setGrain(null);
                listOfAvailableCells.add(el);
            }
        }

        fillGridWIthGrains(numberOfGrains);
        startSimulation(probabilityToChange, type);

        listOfCellsOnBorder.clear();

        for (Grain el : listOfSelectedGrainsSubstructure) {
            el.setFrozen(false);
        }

        for (Grain el : listOfSelectedGrainsDualPhase) {
            el.setFrozen(false);
        }

        determineBorders();

        for (Grain el : listOfSelectedGrainsSubstructure) {
            el.setFrozen(true);
        }

        for (Grain el : listOfSelectedGrainsDualPhase) {
            el.setFrozen(true);
        }
    }


    public void startStructureMC(boolean type, int probabilityToChange, int numberOfGrains) {
        List<Grain> listTMP = new ArrayList<>();
        for (Grain el : listOfSelectedGrainsSubstructure) {
            el.setFrozen(true);
            if (!listTMP.contains(el)) listTMP.add(el);
        }

        for (Grain el : listOfSelectedGrainsDualPhase) {
            el.setFrozen(true);
            el.setColor(Grain.DUAL_PHASE_COLOR);
            el.setID(Grain.DUAL_PHASE_ID);

            for (Cell el_c : listOfCells) {
                if (listOfSelectedGrainsDualPhase.contains(el_c.getGrain())) {
                    el_c.setState(DP);
                }
            }

            if (!listTMP.contains(el)) listTMP.add(el);
        }

        for (Cell el : listOfCells) {
            if (!listOfSelectedGrainsSubstructure.contains(el.getGrain()) && !listOfSelectedGrainsDualPhase.contains(el.getGrain())) {
                el.setState(EMPTY);
                el.setOnBorder(false);
                el.setGrain(null);
                listOfAvailableCells.add(el);
            }
        }

        Cell.resetListOfGrains();
        Cell.getListOfGrains().addAll(listTMP);
        fillGridWIthGrainsMC(numberOfGrains);

        listOfCellsOnBorder.clear();

        for (Grain el : listOfSelectedGrainsSubstructure) {
            el.setFrozen(false);
        }

        for (Grain el : listOfSelectedGrainsDualPhase) {
            el.setFrozen(false);
        }

        determineBorders();

        for (Grain el : listOfSelectedGrainsSubstructure) {
            el.setFrozen(true);
        }

        for (Grain el : listOfSelectedGrainsDualPhase) {
            el.setFrozen(true);
        }
    }


    public void clearSpace(int gbSize) {
        listOfSelectedGrainsSubstructure = new ArrayList<>();
        listOfSelectedGrainsDualPhase = new ArrayList<>();

        if (listOfSelectedGrainsGB.size() > 0) {
            for (Cell el : listOfCellsOnBorder) {
                if (listOfSelectedGrainsGB.contains(el.getGrain())) {
                    createInclusion(el.getCords().y, el.getCords().x, gbSize, SQUARE);
                }
            }

            for (Cell el : listOfCells) {
                if (el.getState() != INCLUSION) {
                    Cell.getListOfGrains().remove(el.getGrain());
                    el.setState(EMPTY);
                    el.setGrain(null);
                    el.setOnBorder(false);
                    listOfAvailableCells.add(el);
                }
            }
            listOfSelectedGrainsGB = new ArrayList<>();
        }
    }

    public double countGB() {
        double result = 0.0;
        double counter = 0;
        double area = width * height;

        for (Cell el : listOfCells) {
            if (el.getState().equals(INCLUSION)) {
                counter++;
            }
        }

        result = counter * 100.0 / area;

        return result;
    }

    public void setEnergy(double grainEnergy, double grainEnergySpread, double borderEnergy, double borderEnergySpread){
        double noise;
        for (Cell el : listOfCells) {
            if (el.isOnBorder()){
                noise = ThreadLocalRandom.current().nextDouble(-grainEnergySpread, grainEnergySpread);
                el.setEnergy(grainEnergy + noise);
            } else {
                noise = ThreadLocalRandom.current().nextDouble(-borderEnergySpread, borderEnergySpread);
                el.setEnergy(borderEnergy + noise);
            }
        }
    }

    public double getMinEnergy(){
        double minEnergy = listOfCells.get(0).getEnergy();
        for (Cell el : listOfCells){
            minEnergy = Math.min(el.getEnergy(), minEnergy);
        }
        return minEnergy;
    }

    public double getMaxEnergy(){
        double maxEnergy = listOfCells.get(0).getEnergy();
        for (Cell el : listOfCells){
            maxEnergy = Math.max(el.getEnergy(), maxEnergy);
        }
        return maxEnergy;
    }

}
