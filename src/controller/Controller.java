package controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import model.*;
import model.Cell;
import model.DataIE.BitmapIE;
import model.DataIE.TextFileIE;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import static model.InclusionTypes.*;
import static model.CellState.*;
import static model.StructureTypes.*;

public class Controller implements Initializable {

    public MenuBar menuBar;
    public Canvas canvas;
    public MenuItem menuItemImportDataFile, menuItemImportBitmap, menuItemIExportDataFile, menuItemIExportBitmap;
    public Menu menuImport, menuExport;
    public TextField fieldGrains, fieldX, fieldY, fieldInclusionsAmount, fieldInclusionsSize, fieldProbabilityToChange, fieldGB;
    public Button buttonGrowth, buttonNucleating, buttonClear, buttonAddInclusions, buttonSelectAll, buttonUnselectAll,  buttonStructureStart, buttonClearSpace;
    public ChoiceBox<String> choiceBoxInclusionsType, choiceBoxStructureType;
    public CheckBox checkBoxNewGrowth, checkBoxGB;
    public Label labelGB;

    private GraphicsContext gc;
    private Model model;
    private TextFileIE textFileIE;
    private BitmapIE bitmapIE;
    private InclusionTypes inclusionsType;
    private StructureTypes structureTypes;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gc = canvas.getGraphicsContext2D();
        fieldX.setText(String.valueOf(500));
        fieldY.setText(String.valueOf(500));
        fieldProbabilityToChange.setText(String.valueOf(100));

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

        String[] typeOfStructures = new String[]{"Substructure", "Dual phase"};
        choiceBoxStructureType.setItems(FXCollections.observableArrayList(typeOfStructures));
        choiceBoxStructureType.setValue("Substructure");
        structureTypes = SUBSTRUCTURE;
        choiceBoxStructureType.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue.intValue()) {
                case 0:
                    structureTypes = SUBSTRUCTURE;
                    break;
                case 1:
                    structureTypes = DUAL_PHASE;
                    break;
            }
        });

        checkBoxNewGrowth.setSelected(false);
        labelGB.setText("0.0 %");
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
            if (numberOfGrains > model.getListOfAvailableCells().size()){
                numberOfGrains = model.getListOfAvailableCells().size();
            }
//            if (numberOfGrains > ((int) canvas.getWidth() * (int) canvas.getHeight())) {
//                numberOfGrains = (int) canvas.getWidth() * (int) canvas.getHeight();
//            }
            model.fillGridWIthGrains(numberOfGrains);
            showGridOnCanvas();
        }
    }

    public void startGrowth(ActionEvent actionEvent) {
        int probabilityToChange = readValueFromTextField(fieldProbabilityToChange);

        if (model != null) {
            if (checkBoxNewGrowth.isSelected()){
                if (probabilityToChange > 0) {
                    model.startSimulation(probabilityToChange, true);
                    model.determineBorders();
                    showGridOnCanvas();
                }
            } else {
                model.startSimulation(probabilityToChange, false);
                model.determineBorders();
                showGridOnCanvas();
            }
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
                    if (holdCell.getState().equals(GRAIN) && holdCell.getGrain() != null) {
                        gc.setFill(holdCell.getGrain().getColor());
                        if (holdCell.isOnBorder() && (model.getListOfSelectedGrainsSubstructure().contains(holdCell.getGrain()) ||
                                                      model.getListOfSelectedGrainsDualPhase().contains(holdCell.getGrain()))){
                            gc.setFill(Grain.BORDER_COLOR);
                        }
                        gc.fillRect(i, j, 1, 1);
                    } else if (holdCell.getState().equals(INCLUSION)){
                        gc.setFill(Grain.INCLUSION_COLOR);
                        gc.fillRect(i, j, 1, 1);
                    } else if (holdCell.getState().equals(DP)){
                        gc.setFill(Grain.DUAL_PHASE_COLOR);
                        if (holdCell.isOnBorder() && (model.getListOfSelectedGrainsSubstructure().contains(holdCell.getGrain()) ||
                                model.getListOfSelectedGrainsDualPhase().contains(holdCell.getGrain()))){
                            gc.setFill(Grain.BORDER_COLOR);
                        }
                        gc.fillRect(i, j, 1, 1);
                    } else if (holdCell.getState().equals(EMPTY)){
                        gc.setFill(Grain.BACKGROUND_COLOR);
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

    public void clickCanvas(MouseEvent mouseEvent) {
        int x0 = 1, y0 = 25; //współrzędne początka canvasa w okienku
        //współrzędne w okienku
        int x = (int) mouseEvent.getSceneX() - x0;
        int y = (int) mouseEvent.getSceneY() - y0;

        if (model != null){
            if (x > 0 && x < model.getWidth() && y > 0 && y < model.getHeight()){
                Grain holdGrain = model.getGrid()[x][y].getGrain();
                model.addRemoveSelectedGrain(holdGrain, structureTypes);
                showGridOnCanvas();
            }
        }
    }

    public void startStructure(ActionEvent actionEvent) {
        int probabilityToChange = readValueFromTextField(fieldProbabilityToChange);
        int numberOfGrains = readValueFromTextField(fieldGrains);
        boolean type = checkBoxNewGrowth.isSelected();

        if (model != null){
            if (model.getListOfSelectedGrainsSubstructure().size() > 0 || model.getListOfSelectedGrainsDualPhase().size() > 0){
                if (probabilityToChange > 0) {
                    model.startStructure(type, probabilityToChange, numberOfGrains);
                }
            }
            showGridOnCanvas();
        }
    }

    public void startSelectAll(ActionEvent actionEvent) {
        if (model != null){
            if (structureTypes.equals(SUBSTRUCTURE)){
                model.setListOfSelectedGrainsDualPhase(new ArrayList<>());
            } else if (structureTypes.equals(DUAL_PHASE)){
                model.setListOfSelectedGrainsSubstructure(new ArrayList<>());
            }
            model.setListOfSelectedGrainsGB(new ArrayList<>());

            for (Grain el : Cell.getListOfGrains()){
                el.setFrozen(true);
                if (structureTypes.equals(SUBSTRUCTURE)){
                    model.addElementToListOfSelectedGrainsSubstructure(el);
                } else if (structureTypes.equals(DUAL_PHASE)){
                    model.addElementToListOfSelectedGrainsDualPhase(el);
                }
                model.addElementToListOfSelectedGrainsGB(el);
            }
            showGridOnCanvas();
        }
    }

    public void startUnselectAll(ActionEvent actionEvent) {
        if (model != null){
            for (Grain el : Cell.getListOfGrains()){
                el.setFrozen(false);
            }
            model.setListOfSelectedGrainsDualPhase(new ArrayList<>());
            model.setListOfSelectedGrainsSubstructure(new ArrayList<>());
            model.setListOfSelectedGrainsGB(new ArrayList<>());
            showGridOnCanvas();
        }
    }

    public void startClearSpace(ActionEvent actionEvent) {
        int gbSize = readValueFromTextField(fieldGB);

        if (model != null && checkBoxGB.isSelected()){
            model.clearSpace(gbSize);
            showGridOnCanvas();
            model.setChanged(false);
            model.setChangedRandom(false);

            labelGB.setText(String.valueOf(model.countGB()) + " %");
        }
    }
}
