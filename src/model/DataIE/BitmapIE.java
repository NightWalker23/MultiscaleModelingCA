package model.DataIE;

import model.Model;

public class BitmapIE extends DataIE {
    private static BitmapIE bitmapIE;

    private BitmapIE() {
    }

    public static BitmapIE getInstance(){
        if (bitmapIE == null)
            bitmapIE = new BitmapIE();

        return bitmapIE;
    }

    @Override
    public void exportData(Model model) {

    }

    @Override
    public Model importData() {
        return null;
    }
}
