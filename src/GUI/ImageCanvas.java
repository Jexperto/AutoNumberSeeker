package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;


// Да, я делаю форму не через конструктор, потому что он убогий. Как говорил Андрей: "Можете лучше - делайте".
public class ImageCanvas extends JPanel {


    BufferedImage originalBufferedImage;
    private final int WIDTH = 800;
    private final int HEIGHT = 500;

    ImageCanvas(BufferedImage originalBufferedImage) {
        this.originalBufferedImage = originalBufferedImage;
        setBounds(0, 0, WIDTH, HEIGHT);
        repaint();

    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        //g2d.drawImage(originalBufferedImage.getScaledInstance(600,200,Image.SCALE_DEFAULT),0,0, (img, infoflags, x, y, width, height) -> false);
        g.drawImage(originalBufferedImage, 0, 0, WIDTH, HEIGHT, (img, infoflags, x, y, width, height) -> false);
    }


    private Image getScaledImage(BufferedImage bf) {
        return bf.getScaledInstance(bf.getWidth(), (int) (bf.getHeight() * getRatio(bf)), Image.SCALE_SMOOTH);
    }

    public void setOriginalBufferedImage(BufferedImage originalBufferedImage) {
        this.originalBufferedImage = originalBufferedImage;
    }

    public float getRatio(BufferedImage originalBufferedImage) {
        int imageHeight = originalBufferedImage.getHeight();
        if (imageHeight > HEIGHT)
            return HEIGHT / imageHeight;
        else
            return imageHeight / HEIGHT;
    }


}
