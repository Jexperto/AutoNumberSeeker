package ImageProcessor;

import java.awt.image.BufferedImage;

public class Processor {

    public static BufferedImage contrastProcessor(BufferedImage origImage) {
        BufferedImage work = new BufferedImage(origImage.getWidth(), origImage.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
        for (int i = 0; i < work.getWidth(); i++) {
            for (int j = 0; j < work.getHeight(); j++) {
                int color = origImage.getRGB(i, j);

                int red = (color >>> 16) & 0xFF;
                int green = (color >>> 8) & 0xFF;
                int blue = color & 0xFF;

                if ((red > 50) && (Math.abs(red - green) < 40) && (Math.abs(red - blue) < 60) && (Math.abs(blue - green) < 40))
                    color = 0x00FFFFFF;

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

//    boolean getNormalImage(BufferedImage normalImage) {
//        // TODO: 18.05.2018 Обработка исходного изображения, для получения изображения номера.
//        BufferedImage workImage = new BufferedImage(originImage.getWidth(), originImage.getHeight(), BufferedImage.TYPE_INT_RGB);
//        //if (getRectangleImage(normalImage)) {
//        rotationToNormal(workImage);
//        normalizeImage(workImage);
//        normalImage = new BufferedImage(workImage.getWidth(), workImage.getHeight(), BufferedImage.TYPE_INT_RGB);
//        normalImage.setData(workImage.getData());
//        //  return true;
//        //}
//        return false;
//    }

//    public BufferedImage testing() {
//        BufferedImage res;
//
//        res = contrastProcessor(originImage);
//
//        System.out.println("testing...");
//        return res;
//    }

//    private class Contrasting implements Runnable{
//
//        private BufferedImage work;
//        private BufferedImage origImage;
//        private int k;
//        private int step;
//
//        public Contrasting(BufferedImage origImage, BufferedImage work, int k, int step) {
//            this.origImage = origImage;
//            this.work = work;
//            this.k = k;
//            this.step = step;
//        }
//
//        @Override
//        public void run() {
//            work = new BufferedImage(origImage.getWidth(), origImage.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
//            for (int i = k; i < work.getWidth(); i+=step) {
//                for (int j = k; j < work.getHeight(); j+=step) {
//                    int color = origImage.getRGB(i, j);
//
//                    int red = (color >>> 16) & 0xFF;
//                    int green = (color >>> 8) & 0xFF;
//                    int blue = color & 0xFF;
//
//                    if ((red > 50) && (Math.abs(red - green) < 40) && (Math.abs(red - blue) < 60) && (Math.abs(blue - green) < 40))
//                        color = 0x00FFFFFF;
//
//                    //схема цвета: 0xAARRGGBB, где AA - прозрачность (не учитывается), RR - красный, GG - зелёный, BB - синий.
//                    //Граничные условия определяют границу разделения цветов на черный и белый.
//                    //На данный момент условия закреплены, если для некоторых изображений будут осечки, придётся делать их динамичными.
//                    if (((color & 0x00ff0000) < 0x00f00000) && ((color & 0x0000ff00) < 0x0000a000) && ((color & 0x000000ff) < 0x000000f0))
//                        work.setRGB(i, j, 0);
//                    else
//                        work.setRGB(i, j, 0x00FFFFFF);
//
//                }
//            }
//        }
//
//    }

}
