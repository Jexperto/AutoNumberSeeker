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

    private BufferedImage[] contrastProcessor(BufferedImage origImage) {
        BufferedImage[] work = new BufferedImage[1];
        work[0] = new BufferedImage(origImage.getWidth(), origImage.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
        //work[1] = new BufferedImage(origImage.getWidth(), origImage.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
        //work[2] = new BufferedImage(origImage.getWidth(), origImage.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
        for (int i = 0; i < work[0].getWidth(); i++) {
            for (int j = 0; j < work[0].getHeight(); j++) {
                int color = origImage.getRGB(i, j);
                //color += 0x00f000f0; // Повышение яркости

                /*
                int lightColor1 = 0;
                int lightColor2 = 0;
                if((((color & 0x00FF0000) + 0x000a0000) & 0xFF000000) == 0)
                    lightColor1 += (color & 0x00FF0000) + 0x000a0000;
                else
                    lightColor1 +=0x00FF0000;
                if(((color & 0x000000FF + 0x0000000a) & 0x0000FF00) == 0)
                    lightColor2 += (color & 0x000000FF) + 0x0000000a;
                else
                    lightColor2 += 0x000000FF;
                lightColor1 += color & 0x0000FF00;
                lightColor2 += color & 0x0000FF00;*/

                int red = (color >>> 16) & 0xFF;
                int green = (color >>> 8) & 0xFF;
                int blue = color & 0xFF;

                if ((red > 50) && (Math.abs(red - green) < 40) && (Math.abs(red - blue) < 60) && (Math.abs(blue - green) < 40))
                    color = 0x00FFFFFF;

                //схема цвета: 0xAARRGGBB, где AA - прозрачность (не учитывается), RR - красный, GG - зелёный, BB - синий.
                //Граничные условия определяют границу разделения цветов на черный и белый.
                //На данный момент условия закреплены, если для некоторых изображений будут осечки, придётся делать их динамичными.
                if (((color & 0x00ff0000) < 0x00f00000) && ((color & 0x0000ff00) < 0x0000a000) && ((color & 0x000000ff) < 0x000000f0))
                    work[0].setRGB(i, j, 0);
                else
                    work[0].setRGB(i, j, 0x00FFFFFF);

//                if (((lightColor1 & 0x00ff0000) < 0x00f00000) && ((lightColor1 & 0x0000ff00) < 0x0000a000) && ((lightColor1 & 0x000000ff) < 0x000000f0))
//                    work[1].setRGB(i, j, 0);
//                else
//                    work[1].setRGB(i, j, 0x00FFFFFF);
//
//                if (((lightColor2 & 0x00ff0000) < 0x00f00000) && ((lightColor2 & 0x0000ff00) < 0x0000a000) && ((lightColor2 & 0x000000ff) < 0x000000f0))
//                    work[1].setRGB(i, j, 0);
//                else
//                    work[1].setRGB(i, j, 0x00FFFFFF);
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
        BufferedImage[] res;

        res = contrastProcessor(originImage);

        System.out.println("testing...");
        if (res == null)
            return;

        try {
            ImageIO.write(res[0], "png", new File("testRes1.png"));
            //ImageIO.write(res[1], "png", new File("testRes2.png"));
            //ImageIO.write(res[2], "png", new File("testRes3.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
