package model;

public enum CellState {
    EMPTY, GRAIN, INCLUSION, DP;

    public static CellState fromString(String type){
        switch (type){
            case "EMPTY":
                return EMPTY;
            case "GRAIN":
                return GRAIN;
            case "INCLUSION":
                return INCLUSION;
            case "DP":
                return DP;
        }
        return EMPTY;
    }
}
