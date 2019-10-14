package controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import model.Cell;
import model.CellState;
import model.DataIE.BitmapIE;
import model.DataIE.TextFileIE;
import model.Model;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public MenuBar menuBar;
    public Canvas canvas;
    public MenuItem menuItemImportDataFile, menuItemImportBitmap, menuItemIExportDataFile, menuItemIExportBitmap;
    public Menu menuImport, menuExport;
    public TextField fieldGrains, fieldX, fieldY;//, fieldMCS, fieldInclusionsAmount, fieldPercent, fieldInclusionsSize;
    //public RadioButton radioCA, radioMC;
    public Button buttonGrowth, buttonNucleating, buttonClear;// buttonAddInclusions, buttonSelectAll, ;
    //public CheckBox checkBoxShape, checkBoxSelectN, checkBoxSUB;
    //public ChoiceBox choiceBoxInclusionsType;

    private GraphicsContext gc;
    private Model model;
    private TextFileIE textFileIE;
    private BitmapIE bitmapIE;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gc = canvas.getGraphicsContext2D();
        fieldX.setText(String.valueOf(500));
        fieldY.setText(String.valueOf(500));
    }

    public void importDataFile(ActionEvent actionEvent) {
        textFileIE = TextFileIE.getInstance();
        model = textFileIE.importData();
        if (model != null) {
            setFieldsText();
            cleanCanvas();
            showGridOnCanvas();
        }
    }

    public void importBitmap(ActionEvent actionEvent) {
        bitmapIE = BitmapIE.getInstance();
        model = bitmapIE.importData();
        if (model != null) {
            setFieldsText();
            cleanCanvas();
            showGridOnCanvas();
        }
    }

    public void exportDataFile(ActionEvent actionEvent) {
        textFileIE = TextFileIE.getInstance();
        textFileIE.exportData(model);
    }

    public void exportBitmap(ActionEvent actionEvent) {
        bitmapIE = BitmapIE.getInstance();
        bitmapIE.exportData(model, canvas);
    }

    public void startNucleating(ActionEvent actionEvent) {
        int numberOfGrains;

        cleanCanvas();
        createNewModel();

        if (model != null) {
            numberOfGrains = readValueFromTextField(fieldGrains);
            if (numberOfGrains > ((int) canvas.getWidth() * (int) canvas.getHeight())) {
                numberOfGrains = (int) canvas.getWidth() * (int) canvas.getHeight();
            }
            model.fillGridWIthGrains(numberOfGrains);
            showGridOnCanvas();
        }
    }

    public void startGrowth(ActionEvent actionEvent) {
        if (model != null) {
            model.startSimulation();
            model.determineBorders();
            showGridOnCanvas();
        }
    }

    public void startClear(ActionEvent actionEvent) {
        cleanCanvas();
        createNewModel();
    }

    public void startAddInclusions(ActionEvent actionEvent) {
    }

    public void startSelectAll(ActionEvent actionEvent) {
    }

    private void cleanCanvas() {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getHeight(), canvas.getWidth());
    }

    private void createNewModel() {
        int x, y;
        x = readValueFromTextField(fieldY);
        y = readValueFromTextField(fieldX);

        if (x > (int) canvas.getWidth()) {
            x = (int) canvas.getWidth();
        }
        if (x < 0) {
            x = 0;
        }
        if (y > (int) canvas.getHeight()) {
            y = (int) canvas.getHeight();
        }
        if (y < 0) {
            y = 0;
        }

        model = new Model(y, x);
        setFieldsText();
    }

    private void showGridOnCanvas() {
        if (model != null) {
            /*TODO:
                przerzucić rysowanie do nowego wątku
            */
            Cell holdGrid[][] = model.getGrid();
            for (int i = 0; i < model.getWidth(); i++) {
                for (int j = 0; j < model.getHeight(); j++) {
                    Cell holdCell = holdGrid[i][j];
                    if (holdCell.getState().equals(CellState.GRAIN)) {
                        gc.setFill(holdCell.getGrain().getColor());
//                        if (holdCell.isOnBorder()) gc.setFill(Grain.BORDER_COLOR);    //do rysowania krawędzi
                        gc.fillRect(i, j, 1, 1);
                    }
                }
            }
        }
    }

    private int readValueFromTextField(TextField field) {
        int value = 0;

        try {
            value = Integer.parseInt(field.getText());
        } catch (NumberFormatException ignored) {
        }

        return value;
    }

    private void setFieldsText() {
        if (model != null) {
            fieldY.setText(String.valueOf(model.getHeight()));
            fieldX.setText(String.valueOf(model.getWidth()));
        }
    }

}
