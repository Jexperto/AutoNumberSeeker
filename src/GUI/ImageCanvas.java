package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;


// Да, я делаю форму не через конструктор, потому что он убогий. Как говорил Андрей: "Можете лучше - делайте".
public class ImageCanvas extends JPanel {

    private final boolean XSCALE = true;
    private final boolean YSCALE = false;

    private BufferedImage originalBufferedImage;
    private final int WIDTH = 800;
    private final int HEIGHT = 500;

    ImageCanvas(/*BufferedImage originalBufferedImage*/) {
//        this.originalBufferedImage = originalBufferedImage;
        setBounds(0, 0, WIDTH, HEIGHT);
//        repaint();

    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        //g2d.drawImage(originalBufferedImage.getScaledInstance(600,200,Image.SCALE_DEFAULT),0,0, (img, infoflags, x, y, width, height) -> false);
        g.drawImage(scaleImage(originalBufferedImage), 0, 0, (img, infoflags, x, y, width, height) -> false);
    }


    private Image getScaledImage(BufferedImage bf) {
        return bf.getScaledInstance(bf.getWidth(), (int) (bf.getHeight() * getRatio(bf)), Image.SCALE_SMOOTH);
    }

    public void setOriginalBufferedImage(BufferedImage originalBufferedImage) {
        this.originalBufferedImage = originalBufferedImage;
        setBounds(0, 0, originalBufferedImage.getWidth(), originalBufferedImage.getHeight());
    }

    public float getRatio(BufferedImage originalBufferedImage) {
        int imageHeight = originalBufferedImage.getHeight();
        if (imageHeight > HEIGHT)
            return HEIGHT / imageHeight;
        else
            return imageHeight / HEIGHT;
    }

    public BufferedImage scaleImage(BufferedImage originalBufferedImage) {
        if (originalBufferedImage == null) {
            return null;
        }
        double scaleMag = scaleMagnitude(originalBufferedImage);
        int w = originalBufferedImage.getWidth();
        int h = originalBufferedImage.getHeight();
        BufferedImage scaledBufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.scale(scaleMag, scaleMag);
        AffineTransformOp scaleOp =
                new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        scaledBufferedImage = scaleOp.filter(originalBufferedImage, scaledBufferedImage);
        return scaledBufferedImage.getSubimage(0, 0, (int) (w * scaleMag), (int) (h * scaleMag));

    }

    public double scaleMagnitude(BufferedImage originalBufferedImage) {
        return originalBufferedImage.getWidth() < originalBufferedImage.getHeight() ? (double) WIDTH / originalBufferedImage.getWidth() : (double) HEIGHT / originalBufferedImage.getHeight();
    }


}
