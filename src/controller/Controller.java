package controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import model.*;
import model.Cell;
import model.DataIE.BitmapIE;
import model.DataIE.TextFileIE;

import java.net.URL;
import java.util.ResourceBundle;
import static model.InclusionTypes.*;
import static model.CellState.*;

public class Controller implements Initializable {

    public MenuBar menuBar;
    public Canvas canvas;
    public MenuItem menuItemImportDataFile, menuItemImportBitmap, menuItemIExportDataFile, menuItemIExportBitmap;
    public Menu menuImport, menuExport;
    public TextField fieldGrains, fieldX, fieldY, fieldInclusionsAmount, fieldInclusionsSize;
    public Button buttonGrowth, buttonNucleating, buttonClear;
    public ChoiceBox<String> choiceBoxInclusionsType;
    public Button buttonAddInclusions;

    private GraphicsContext gc;
    private Model model;
    private TextFileIE textFileIE;
    private BitmapIE bitmapIE;
    private InclusionTypes inclusionsType;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gc = canvas.getGraphicsContext2D();
        fieldX.setText(String.valueOf(500));
        fieldY.setText(String.valueOf(500));

        String[] typeOfInclusions = new String[]{"Square", "Circle"};
        choiceBoxInclusionsType.setItems(FXCollections.observableArrayList(typeOfInclusions));
        choiceBoxInclusionsType.setValue("Square");
        inclusionsType = SQUARE;
        choiceBoxInclusionsType.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue.intValue()) {
                case 0:
                    inclusionsType = SQUARE;
                    break;
                case 1:
                    inclusionsType = CIRCLE;
                    break;
            }
        });
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
        int x, y;
        x = readValueFromTextField(fieldY);
        y = readValueFromTextField(fieldX);

        cleanCanvas();
        if (model != null){
            if (model.getWidth() != y || model.getHeight() != x){
                createNewModel();
            }
        } else {
            createNewModel();
        }


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
        int amount = readValueFromTextField(fieldInclusionsAmount);
        int radius = readValueFromTextField(fieldInclusionsSize);
        if (model == null){
            createNewModel();
        }
        model.addInclusions(inclusionsType, amount, radius);
        showGridOnCanvas();
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
                    if (holdCell.getState().equals(GRAIN)) {
                        gc.setFill(holdCell.getGrain().getColor());
//                        if (holdCell.isOnBorder()) gc.setFill(Grain.BORDER_COLOR);    //do rysowania krawędzi
                        gc.fillRect(i, j, 1, 1);
                    } else if (holdCell.getState().equals(INCLUSION)){
                        gc.setFill(Grain.INCLUSION_COLOR);
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
