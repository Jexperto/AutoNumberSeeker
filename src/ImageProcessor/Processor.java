package ImageProcessor;

import java.awt.image.BufferedImage;

public class Processor {

    private BufferedImage originImage;

    public Processor(BufferedImage originImage) {
        this.originImage = originImage;
    }

    private boolean getRectangleImage(BufferedImage rectImage) {
        // TODO: 18.05.2018 Должна выделять и возвращать буфер с прямоугольником номера.
        return false;
    }

    private void rotationToNormal(BufferedImage rotImage) {
        // TODO: 18.05.2018 Должна повернуть изображение, если оно кривое.
    }

    //Возможно уберу.
    private void normalizeImage(BufferedImage normImage) {
        // TODO: 18.05.2018 Сложная обработка, если знак повернут по осям.
    }

    boolean getNormalImage(BufferedImage normalImage) {
        // TODO: 18.05.2018 Обработка исходного изображения, для получения изображения номера.
        BufferedImage workImage = new BufferedImage(originImage.getWidth(), originImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        rotationToNormal(workImage);
        if (getRectangleImage(workImage)) {
            normalizeImage(workImage);
            normalImage = new BufferedImage(workImage.getWidth(), workImage.getHeight(), BufferedImage.TYPE_INT_RGB);
            normalImage.setData(workImage.getData());
            return true;
        }
        return false;
    }

}
