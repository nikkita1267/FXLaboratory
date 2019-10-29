package sample;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Application {
    private Logger logger = Logger.getLogger("Main logger");
    private Color penColor = Color.BLACK;

    @Override
    public void start(Stage primaryStage) {
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Canvas canvas = new Canvas(screenSize.width / 6., screenSize.height / 3.);
        final GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.setStroke(penColor);
        graphicsContext.setLineWidth(1);

        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED,
                event -> {
                    graphicsContext.beginPath();
                    graphicsContext.moveTo(event.getX(), event.getY());
                    graphicsContext.stroke();
                });

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                event -> {
                    graphicsContext.lineTo(event.getX(), event.getY());
                    graphicsContext.stroke();
                });


        GridPane root = new GridPane();

        Button saveButton = new Button("Save snapshot");
        Button loadButton = new Button("Load previous snapshot");
        Button newCanvasButton = new Button("Clear canvas");

        saveButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            logger.log(Level.INFO, "save snapshot");

            WritableImage snapshotOfCanvas = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
            canvas.snapshot(new SnapshotParameters(), snapshotOfCanvas);
            try {
                FileChooser saveToFile = new FileChooser();
                saveToFile.setTitle("Save snapshot");
                saveToFile.getExtensionFilters()
                        .add(new FileChooser.ExtensionFilter("png", "png", "PNG"));
                saveToFile.setInitialFileName("snapshot.png");
                File choosedFile = saveToFile.showSaveDialog(primaryStage);
                if (choosedFile == null) return;
                ImageIO.write(SwingFXUtils.fromFXImage(snapshotOfCanvas, null), "PNG", choosedFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        loadButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            logger.log(Level.INFO, "load snapshot");

            FileChooser loadFromFile = new FileChooser();
            loadFromFile.setTitle("Load snapshot");
            File choosedFile = loadFromFile.showOpenDialog(primaryStage);
            if (choosedFile == null) return;
            Image savedImage = null;
            try {
                savedImage = new Image(new FileInputStream(choosedFile));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            graphicsContext.drawImage(savedImage, 0, 0);
            logger.log(Level.INFO, "loaded");
        });

        newCanvasButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            logger.log(Level.INFO, "clear canvas");
            graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        });

        ColorPicker picker = new ColorPicker(penColor);
        picker.setOnAction(event -> {
            logger.log(Level.INFO, "change color");
            penColor = picker.getValue();
            graphicsContext.setStroke(penColor);
        });
        canvas.setCursor(Cursor.CROSSHAIR);
        canvas.getGraphicsContext2D();

        root.getColumnConstraints().add(new ColumnConstraints(screenSize.width / 6.));
        root.getColumnConstraints().add(new ColumnConstraints(screenSize.width / 6.));
        root.getRowConstraints().add(new RowConstraints(2 * screenSize.height / 15.));
        root.getRowConstraints().add(new RowConstraints(screenSize.height / 15.));
        root.getRowConstraints().add(new RowConstraints(screenSize.height / 15.));
        root.getRowConstraints().add(new RowConstraints(screenSize.height / 15.));
        root.add(canvas, 0, 0, 1, 4);
        root.add(picker, 1, 0, 1, 1);
        root.add(saveButton, 1, 2, 1, 1);
        root.add(loadButton, 1, 1, 1, 1);
        root.add(newCanvasButton, 1, 3, 1, 1);
        Scene scene = new Scene(root, screenSize.width / 3.0, screenSize.height / 3.0);
        primaryStage.setTitle("Paint 0.5");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
