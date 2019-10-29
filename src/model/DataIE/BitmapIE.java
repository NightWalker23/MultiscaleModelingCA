package model.DataIE;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import model.Cell;
import model.Grain;
import model.Model;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class BitmapIE {
    private static BitmapIE bitmapIE;

    private BitmapIE() {
    }

    public static BitmapIE getInstance() {
        if (bitmapIE == null)
            bitmapIE = new BitmapIE();

        return bitmapIE;
    }

    public void exportData(Model model, Canvas canvas) {
        //if (model != null && model.getListOfAvailableCells().size() == 0) {
        if (model != null && model.isSimulationFinished()) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File("./"));

            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("bmp files (*.bmp)", "*.bmp");
            fileChooser.getExtensionFilters().add(extFilter);

            File file = fileChooser.showSaveDialog(null);

            if (file != null) {
                try {
                    WritableImage writableImage = new WritableImage(model.getWidth(), model.getHeight());
                    canvas.snapshot(null, writableImage);
                    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);
                    ImageIO.write(bufferedImage, "png", file);
                } catch (Exception ignored) {
                }
            }
        }
    }

    public Model importData() {
        /*
        TODO: dodać tą funkcję do wykonywania w nowym wątku
         */
        Model modelBitmmap = null;
        Cell.resetListOfGrains();

        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("BMP FILES", "bmp");
        chooser.setFileFilter(filter);
        chooser.setCurrentDirectory(new File("./"));

        int color;
        int ret = chooser.showOpenDialog(null);

        if (ret == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedImage image = ImageIO.read(chooser.getSelectedFile());
                modelBitmmap = new Model(image.getWidth(), image.getHeight());
                modelBitmmap.setListOfAvailableCells(new ArrayList<>());

                Color colorFromRGB;
                for (int xPixel = 0; xPixel < image.getWidth(); xPixel++) {
                    for (int yPixel = 0; yPixel < image.getHeight(); yPixel++) {
                        color = image.getRGB(xPixel, yPixel);
                        colorFromRGB = Color.rgb((color >> 16) & 0xFF, (color >> 8) & 0xFF, ((color >> 0) & 0xFF));
                        modelBitmmap.getGrid()[xPixel][yPixel].createNewGrain(colorFromRGB);
                        if (colorFromRGB.equals(Grain.BACKGROUND_COLOR)){
                            modelBitmmap.getListOfAvailableCells().add(modelBitmmap.getGrid()[xPixel][yPixel]);
                        }
                    }
                }
                modelBitmmap.determineBorders();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return modelBitmmap;
    }
}
