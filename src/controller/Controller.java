package controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import model.Cell;
import model.CellState;
import model.Grain;
import model.Model;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public MenuBar menuBar;
    public Canvas canvas;
    public MenuItem menuItemImportDataFile, menuItemImportBitmap, menuItemIExportDataFile, menuItemIExportBitmap;
    public Menu menuImport, menuExport;
    public TextField fieldGrains, fieldX, fieldY, fieldMCS, fieldInclusionsAmount, fieldPercent, fieldInclusionsSize;
    public RadioButton radioCA, radioMC;
    public Button buttonGrowth, buttonNucleating, buttonAddInclusions, buttonSelectAll, buttonClear;
    public CheckBox checkBoxShape, checkBoxSelectN, checkBoxSUB;
    public ChoiceBox choiceBoxInclusionsType;

    private GraphicsContext gc;
    private Model model;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gc = canvas.getGraphicsContext2D();
//        cleanCanvas();
//        createNewModel();
    }

    public void importDataFile(ActionEvent actionEvent) {
    }

    public void importBitmap(ActionEvent actionEvent) {
    }

    public void exportDataFile(ActionEvent actionEvent) {
    }

    public void exportBitmap(ActionEvent actionEvent) {
    }

    public void startNucleating(ActionEvent actionEvent) {
        int numberOfGrains;

        cleanCanvas();
        createNewModel();

        if (model != null){
            numberOfGrains = readValueFromTextField(fieldGrains);
            if (numberOfGrains >((int) canvas.getWidth() * (int) canvas.getHeight())){
                numberOfGrains = (int) canvas.getWidth() * (int) canvas.getHeight();
            }
            model.fillGridWIthGrains(numberOfGrains);
            showGridOnCanvas();
        }
    }

    public void startGrowth(ActionEvent actionEvent) {
        if (model != null){
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
//        model = new Model((int)canvas.getWidth(), (int)canvas.getHeight());
        int x, y;
        x = readValueFromTextField(fieldY);
        y = readValueFromTextField(fieldX);

        if (x > (int)canvas.getWidth()) {
            x = (int) canvas.getWidth();
            fieldX.setText(String.valueOf(x));
        }
        if (x < 0) {
            x = 0;
            fieldX.setText(String.valueOf(x));
        }
        if (y > (int)canvas.getHeight()) {
            y = (int) canvas.getHeight();
            fieldX.setText(String.valueOf(y));
        }
        if (y < 0) {
            y = 0;
            fieldX.setText(String.valueOf(y));
        }

        model = new Model(y, x);
    }

    private void showGridOnCanvas() {
        if (model != null){
            /*TODO:
                przerzucić rysowanie do nowego wątku
            */
            Cell holdGrid[][] = model.getGrid();
            for (int i = 0; i < model.getWidth(); i++){
                for (int j = 0; j < model.getHeight(); j++){
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

    private int readValueFromTextField(TextField field){
        int value = 0;

        try{
            value = Integer.parseInt(field.getText());
        } catch (NumberFormatException ignored) {
        }

        return value;
    }

}
