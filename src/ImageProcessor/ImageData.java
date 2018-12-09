package ImageProcessor;


import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.opencv.core.Rect;

import java.io.File;

public class ImageData {
    private File imageFile;
    private byte state;
    private byte currentColor;
    private String number;
    private Label imageState = new Label();
    private WritableImage writableImage;
    private Rectangle rectangle;
    private Rect[] rectangleSet;
    private PixelWriter pixelWriter;

    public ImageData(File imageFile) {
        this.imageFile = imageFile;
        rectangle = new Rectangle();
        rectangle.setFill(Color.YELLOW);
        rectangle.setStroke(Color.RED);
        rectangle.setStrokeWidth(4);

    }

    public Image getWritableImage() {
        if (writableImage == null) {

            Image image = new Image("file:///" + imageFile.getAbsolutePath());
            writableImage = new WritableImage(image.getPixelReader(), (int) image.getWidth(), (int) image.getHeight());
            pixelWriter = writableImage.getPixelWriter();

        }
        return writableImage;
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

    public void displayRect() {
        rectangle.setVisible(true);

        System.out.println("displayed rect");
    }


    private void setRect(int x1, int y1, int width1, int height1) {
        final int x = x1;
        final int y = y1;
        final int width = width1;
        final int height = height1;
            if (pixelWriter == null)
                getWritableImage();
            rectangle.setX(x);
            rectangle.setY(y);
            rectangle.setWidth(width);
            rectangle.setHeight(height);

            for (int i = x; i < x + width - 1; i++) {
                pixelWriter.setColor(i, y, Color.GREEN);
                pixelWriter.setColor(i, y + 1, Color.GREEN);
                pixelWriter.setColor(i, y + 2, Color.GREEN);
                pixelWriter.setColor(i, y + 3, Color.GREEN);
                pixelWriter.setColor(i, y + height - 1, Color.GREEN);
                pixelWriter.setColor(i, y + height - 2, Color.GREEN);
                pixelWriter.setColor(i, y + height - 3, Color.GREEN);
                pixelWriter.setColor(i, y + height - 4, Color.GREEN);
            }
            for (int i = y + 1; i < y + height - 2; i++) {
                pixelWriter.setColor(x, i, Color.GREEN);
                pixelWriter.setColor(x + 1, i, Color.GREEN);
                pixelWriter.setColor(x + 2, i, Color.GREEN);
                pixelWriter.setColor(x + 3, i, Color.GREEN);
                pixelWriter.setColor(x + width - 1, i, Color.GREEN);
                pixelWriter.setColor(x + width - 2, i, Color.GREEN);
                pixelWriter.setColor(x + width - 3, i, Color.GREEN);
                pixelWriter.setColor(x + width - 4, i, Color.GREEN);
            }

            System.out.println(imageFile.getName() + ": " + x + " " + y + " " + width + " " + height);

    }

    public Rectangle getRect() {
        return rectangle;
    }

    public Rect getRect(int index) {
        return rectangleSet != null ? rectangleSet[index] : null;
    }

    public void setRectangleSet(Rect[] rectangleSet) {
        this.rectangleSet = rectangleSet;
        for (Rect rect : rectangleSet) {
            setRect(rect.x, rect.y, rect.width, rect.height);
        }
    }

    void setNumber(String number) {
        this.number = number;
    }

    public File getImageFile() {
        return imageFile;
    }
}
