package model;

public enum CellState {
    EMPTY, GRAIN, INCLUSION, MARTENSITE;

    public static CellState fromString(String type){
        switch (type){
            case "EMPTY":
                return EMPTY;
            case "GRAIN":
                return GRAIN;
            case "INCLUSION":
                return INCLUSION;
            case "MARTENSITE":
                return MARTENSITE;
        }
        return EMPTY;
    }
}
