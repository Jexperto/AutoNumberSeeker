package ImageProcessor;


import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

import java.awt.*;
import java.io.File;

public class ImageData {
    private File imageFile;
    private byte state;
    private byte currentColor;
    private Point point1;
    private Point point2;
    private String number;
    private Label imageState = new Label();

    public ImageData(File imageFile) {
        this.imageFile = imageFile;
        point1 = null;
        point2 = null;
        number = null;
    }

    void requestSetImageState(byte state) {
        this.state = state;
        Platform.runLater(() -> {
            setImageState(state);
        });
    }

    byte getImageState() {
        return state;
    }

    private void setImageState(byte state) {
        switch (state) {
            case 0:
                imageState.setText("В ожидании...");
                break;
            case 1:
                imageState.setText("В процессе...");
                break;
            case 2:
                imageState.setText("Готово");
                break;
            case 3:
                imageState.setText("Ошибка!");
                break;
        }
        if (currentColor != 4)
            setLabelColor(state);
    }

    private void setLabelColor(byte color) {
        currentColor = color;
        switch (color) {
            case 0:
                imageState.setTextFill(new Color(1, 0.54117647058823529, 0, 1));
                break;
            case 1:
                imageState.setTextFill(new Color(0, 0.05490196078431372549019607843137, 0.56862745098039215686274509803922, 1));
                break;
            case 2:
                imageState.setTextFill(new Color(0.06274509803921568627450980392157, 0.7960784313725490196078431372549, 0, 1));
                break;
            case 3:
                imageState.setTextFill(new Color(1, 0, 0, 1));
                break;
            case 4:
                imageState.setTextFill(new Color(1, 1, 1, 1));
                break;
        }
    }

    public void setDefaultColor() {
        setLabelColor(state);
    }

    public void setWhiteColor() {
        setLabelColor((byte) 4);
    }

    public BorderPane createBorderPane() {
        Label imageName = new Label(imageFile.getName());
        BorderPane borderPane = new BorderPane();
        setImageState(state);
        setLabelColor(state);
        borderPane.setLeft(imageName);
        borderPane.setRight(imageState);
        BorderPane.setMargin(imageName, new Insets(0, 0, 0, 20));
        BorderPane.setMargin(imageState, new Insets(0, 20, 0, 0));
        return borderPane;
    }

    void setNumber(String number) {
        this.number = number;
    }

    public File getImageFile() {
        return imageFile;
    }
}
