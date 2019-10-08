package controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import model.Cell;
import model.CellState;
import model.Model;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public MenuBar menuBar;
    public Canvas canvas;
    public MenuItem menuItemImportDataFile, menuItemImportBitmap, menuItemIExportDataFile, menuItemIExportBitmap;
    public Menu menuImport, menuExport;
    public TextField fieldGrains, fieldMCS, fieldInclusionsAmount, fieldPercent, fieldInclusionsSize;
    public RadioButton radioCA, radioMC;
    public Button buttonGrowth, buttonNucleating, buttonAddInclusions, buttonSelectAll, buttonClear;
    public CheckBox checkBoxShape, checkBoxSelectN, checkBoxSUB;
    public ChoiceBox choiceBoxInclusionsType;

    private GraphicsContext gc;
    private Model model;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gc = canvas.getGraphicsContext2D();
        cleanCanvas();
        createNewModel();
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
        int numberOfGrains = 0;

        try{
            numberOfGrains = Integer.parseInt(fieldGrains.getText());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (model != null){
            model.fillGridWIthGrains(numberOfGrains);
            showGridOnCanvas();
        }
    }

    public void startGrowth(ActionEvent actionEvent) {
    }

    public void startClear(ActionEvent actionEvent) {
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
        model = new Model((int)canvas.getWidth(), (int)canvas.getHeight());
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
                        gc.fillRect(i, j, 1, 1);
                    }
                }
            }
        }
    }

}
