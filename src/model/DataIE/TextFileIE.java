package model.DataIE;

import javafx.scene.paint.Color;
import model.Cell;
import model.CellState;
import model.Model;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class TextFileIE extends DataIE {

    private static TextFileIE textFileIE;

    private TextFileIE() {
    }

    public static TextFileIE getInstance() {
        if (textFileIE == null)
            textFileIE = new TextFileIE();

        return textFileIE;
    }

    @Override
    public void exportData(Model model) {
        if (model != null && model.getListOfAvailableCells().size() == 0) {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File("./"));

            int ret = chooser.showSaveDialog(null);
            if (ret == JFileChooser.APPROVE_OPTION) {
                try {
                    int x = model.getWidth(), y = model.getHeight();

                    FileWriter fw = new FileWriter(chooser.getSelectedFile() + ".txt");
                    fw.write(x + "\t" + y);

                    for (int i = 0; i < x; i++) {
                        for (int j = 0; j < y; j++) {
                            Cell holdCell = model.getGrid()[i][j];
                            fw.write("\n" + i + "\t" + j + "\t" +
                                    holdCell.getGrain().getID() + "\t" +
                                    holdCell.getGrain().getColor() + "\t" +
                                    holdCell.getState() + "\t" +
                                    holdCell.isOnBorder());
                        }
                    }

                    fw.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public Model importData() {
        Model modelBitmap = null   ;

        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("./"));

        int ret = chooser.showOpenDialog(null);
        if (ret == JFileChooser.APPROVE_OPTION) {
            try {
                int x, y, ID;
                CellState state;
                Color color;
                boolean onBorder;

                BufferedReader br = new BufferedReader(new FileReader(chooser.getSelectedFile()));
                String line = br.readLine();
                String[] splittedLine = line.split("\t");
                x = Integer.parseInt(splittedLine[0]);
                y = Integer.parseInt(splittedLine[1]);
                modelBitmap = new Model(x, y);

                line = br.readLine();

                while (line != null){
                    splittedLine = line.split("\t");
                    x = Integer.parseInt(splittedLine[0]);
                    y = Integer.parseInt(splittedLine[1]);
                    ID = Integer.parseInt(splittedLine[2]);
                    color = Color.valueOf(splittedLine[3]);
                    state = CellState.fromString(splittedLine[4]);
                    onBorder = Boolean.parseBoolean(splittedLine[5]);

                    modelBitmap.getGrid()[x][y].turnToGrain(ID, state, color, onBorder);
                    line = br.readLine();
                }

                br.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return modelBitmap;
    }
}
