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

    ImageCanvas(/*BufferedImage originalBufferedImage*/) {
//        this.originalBufferedImage = originalBufferedImage;
        setBounds(0, 0, WIDTH, HEIGHT);
//        repaint();

    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        //g2d.drawImage(originalBufferedImage.getScaledInstance(600,200,Image.SCALE_DEFAULT),0,0, (img, infoflags, x, y, width, height) -> false);
        g.drawImage(originalBufferedImage, 0, 0, (img, infoflags, x, y, width, height) -> false);
    }

    public void setOriginalBufferedImage(BufferedImage originalBufferedImage) {
        this.originalBufferedImage = originalBufferedImage;
        setBounds(0, 0, originalBufferedImage.getWidth(), originalBufferedImage.getHeight());
    }

    public BufferedImage scaleImage(BufferedImage originalBufferedImage) {
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
        return scaledBufferedImage.getSubimage(0, 0, (int) (w * scaleMag), (int) (h * scaleMag));

    }

    public double scaleMagnitude(BufferedImage originalBufferedImage) {
        if (originalBufferedImage.getHeight() < HEIGHT && originalBufferedImage.getWidth() < WIDTH)
            return -1;
//           return originalBufferedImage.getWidth() > originalBufferedImage.getHeight() ? (double) WIDTH / originalBufferedImage.getWidth() : (double) HEIGHT / originalBufferedImage.getHeight();
        return Math.min((double) WIDTH / originalBufferedImage.getWidth(), (double) HEIGHT / originalBufferedImage.getHeight());

    }


}
