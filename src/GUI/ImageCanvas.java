package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;


public class ImageCanvas extends JPanel {

    private BufferedImage originalBufferedImage;
    private final int WIDTH = 800;
    private final int HEIGHT = 500;

    ImageCanvas() {
        setBounds(0, 0, WIDTH, HEIGHT);
    }

    //Перерисовка компонента
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (originalBufferedImage != null)
            g.drawImage(originalBufferedImage, 0, 0, (img, infoflags, x, y, width, height) -> false);
    }

    //Установка изображения для отрисовки
    void setOriginalBufferedImage(BufferedImage originalBufferedImage) {
        this.originalBufferedImage = originalBufferedImage;
        if (originalBufferedImage != null)
            setBounds(0, 0, originalBufferedImage.getWidth(), originalBufferedImage.getHeight());
        else
            setBounds(0, 0, WIDTH, HEIGHT);
    }

    //Маштабирование изображения по размерам области отрисовки
    BufferedImage scaleImage(BufferedImage originalBufferedImage) {
        if (originalBufferedImage == null) {
            return null;
        }
        double scaleMag = scaleMagnitude(originalBufferedImage);
        if (scaleMag < 0)
            return originalBufferedImage;
        int w = originalBufferedImage.getWidth();
        int h = originalBufferedImage.getHeight();
        BufferedImage scaledBufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.scale(scaleMag, scaleMag);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        scaledBufferedImage = scaleOp.filter(originalBufferedImage, scaledBufferedImage);
        //Афф преобразования не меняют фактический размер, поэтому выполняется вырезание области
        return scaledBufferedImage.getSubimage(0, 0, (int) (w * scaleMag), (int) (h * scaleMag));

    }

    //Вычисление коэффициента маштабирования. Если не нужно, вернёт -1
    private double scaleMagnitude(BufferedImage originalBufferedImage) {
        if (originalBufferedImage.getHeight() < HEIGHT && originalBufferedImage.getWidth() < WIDTH)
            return -1;
        return Math.min((double) WIDTH / originalBufferedImage.getWidth(), (double) HEIGHT / originalBufferedImage.getHeight());
    }


}
