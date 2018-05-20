package ImageProcessor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Processor {

    private BufferedImage originImage;

    public Processor(BufferedImage originImage) {
        this.originImage = originImage;
    }

    private BufferedImage contrastProcessor(BufferedImage origImage) {
        BufferedImage work = new BufferedImage(origImage.getWidth(), origImage.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
        for (int i = 0; i < work.getWidth(); i++) {
            for (int j = 0; j < work.getHeight(); j++) {
                int color = origImage.getRGB(i, j);
                //color += 0x00f000f0; // Повышение яркости
                //схема цвета: 0xAARRGGBB, где AA - прозрачность (не учитывается), RR - красный, GG - зелёный, BB - синий.
                //Граничные условия определяют границу разделения цветов на черный и белый.
                //На данный момент условия закреплены, если для некоторых изображений будут осечки, придётся делать их динамичными.
                if (((color & 0x00ff0000) < 0x00f00000) && ((color & 0x0000ff00) < 0x0000a000) && ((color & 0x000000ff) < 0x000000f0))
                    work.setRGB(i, j, 0);
                else
                    work.setRGB(i, j, 0x00FFFFFF);

            }
        }
        return work;
    }

    private BufferedImage getRectangleImage(BufferedImage origImage) {
        // TODO: 18.05.2018 Должна выделять и возвращать буфер с прямоугольником номера.

        return null;
    }

    private BufferedImage rotationToNormal(BufferedImage orignImage) {
        // TODO: 18.05.2018 Должна повернуть изображение, если оно кривое.
        return null;
    }

    //Возможно уберу.
    private BufferedImage normalizeImage(BufferedImage orignImage) {
        // TODO: 18.05.2018 Сложная обработка, если знак повернут по осям.
        return null;
    }

    private boolean checkAutoNumber(BufferedImage numImage) {
        // TODO: 18.05.2018 Должа проверить, есть ли на картинке номер.
        return false;
    }

    boolean getNormalImage(BufferedImage normalImage) {
        // TODO: 18.05.2018 Обработка исходного изображения, для получения изображения номера.
        BufferedImage workImage = new BufferedImage(originImage.getWidth(), originImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        //if (getRectangleImage(normalImage)) {
        rotationToNormal(workImage);
        normalizeImage(workImage);
        normalImage = new BufferedImage(workImage.getWidth(), workImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        normalImage.setData(workImage.getData());
        //  return true;
        //}
        return false;
    }

    public void testing() {
        BufferedImage res;

        res = contrastProcessor(originImage);

        System.out.println("testing...");
        if (res == null)
            return;

        try {
            ImageIO.write(res, "png", new File("testRes.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
