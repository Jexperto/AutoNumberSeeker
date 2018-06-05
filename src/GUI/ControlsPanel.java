package GUI;

import ImageProcessor.Processor;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ControlsPanel implements ActionListener {

    private Button openFileButton;
    private Button runButton;
    private TextField filePathField;
    private ImageCanvas imageCanvas;
    private BufferedImage originalBufferedImage;
    private BufferedImage subBufferedImage;
    private Main mainFrame;
    private PointRep pointRep1;
    private PointRep pointRep2;

    ControlsPanel(Main main) {
        openFileButton = new Button("Открыть файл...");
        openFileButton.setBounds(25, 525, 100, 25);
        openFileButton.addActionListener(this);
        runButton = new Button("Тестировать");
        runButton.setBounds(540, 525, 100, 25);
        runButton.addActionListener(this);
        filePathField = new TextField("Путь к файлу...");
        filePathField.setEditable(false);
        filePathField.setBounds(130, 525, 400, 25);
        imageCanvas = new ImageCanvas();

        imageCanvas.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (originalBufferedImage == null)
                    return;
                if (e.getX() > originalBufferedImage.getWidth() || e.getY() > originalBufferedImage.getHeight())
                    return;
                System.out.println("Point set on x = " + e.getX() + ", y = " + e.getY() + ".");
                if (e.getButton() == 1) {
                    if (pointRep1 == null) {
                        pointRep1 = new PointRep(e.getX(), e.getY());
                        System.out.println("Point1 set");
                        imageCanvas.add(pointRep1);
                    } else if (pointRep2 == null && !pointRep1.collis(e.getX(), e.getY())) {
                        System.out.println("Point2 set");
                        pointRep2 = new PointRep(e.getX(), e.getY());
                        imageCanvas.add(pointRep2);
                    }
                }
                if (e.getButton() == 3) {
                    if (pointRep1 != null && pointRep1.collis(e.getX(), e.getY())) {
                        imageCanvas.remove(pointRep1);
                        pointRep1 = null;
                        System.out.println("Point1 unset");
                    } else if (pointRep2 != null && pointRep2.collis(e.getX(), e.getY())) {
                        imageCanvas.remove(pointRep2);
                        pointRep2 = null;
                        System.out.println("Point2 unset");
                    }
                }

                if (pointRep1 != null && pointRep2 != null) {

                    int x = Math.min(pointRep1.x, pointRep2.x);
                    int y = Math.min(pointRep1.y, pointRep2.y);
                    int w = Math.abs(pointRep1.x - pointRep2.x);
                    int h = Math.abs(pointRep1.y - pointRep2.y);

                    int[] mass = new int[w * h];

                    subBufferedImage = new BufferedImage(originalBufferedImage.getWidth(), originalBufferedImage.getHeight(), originalBufferedImage.getType());

                    originalBufferedImage.getRGB(x, y, w, h, mass, 0, w);
                    subBufferedImage.setRGB(x, y, w, h, mass, 0, w);

                    imageCanvas.setOriginalBufferedImage(subBufferedImage);

                } else {

                    imageCanvas.setOriginalBufferedImage(originalBufferedImage);

                }

                imageCanvas.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        mainFrame = main;


        mainFrame.add(openFileButton);
        mainFrame.add(filePathField);
        mainFrame.add(runButton);
        mainFrame.add(imageCanvas);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //Handle open button action.
        if (e.getSource() == openFileButton) {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Images(.png, .jpg, .jpeg, .gif)", "png", "jpg", "jpeg", "gif");
            fileChooser.addChoosableFileFilter(filter);
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setCurrentDirectory(new File("."));
            switch (fileChooser.showOpenDialog(mainFrame)) {
                case JFileChooser.APPROVE_OPTION:
                    File file = fileChooser.getSelectedFile();
                    filePathField.setText(file.getPath());
                    try {
                        originalBufferedImage = ImageIO.read(file);
                        originalBufferedImage = imageCanvas.scaleImage(originalBufferedImage);
                        imageCanvas.setOriginalBufferedImage(originalBufferedImage);
                        imageCanvas.repaint();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    break;
                case JFileChooser.CANCEL_OPTION:
                    System.out.println("Canceled load.");
                    break;
            }
        }
        if (e.getSource() == runButton) {

            System.out.println("doing...");
            if (originalBufferedImage == null) {
                System.out.println("Not image");
                return;
            }
            BufferedImage res;
            if (pointRep1 != null && pointRep2 != null) {

                int x = Math.min(pointRep1.x, pointRep2.x);
                int y = Math.min(pointRep1.y, pointRep2.y);
                int w = Math.abs(pointRep1.x - pointRep2.x);
                int h = Math.abs(pointRep1.y - pointRep2.y);

                imageCanvas.remove(pointRep1);
                imageCanvas.remove(pointRep2);
                pointRep1 = null;
                pointRep2 = null;

                BufferedImage subImage = originalBufferedImage.getSubimage(x, y, w, h);

                res = Processor.contrastProcessor(subImage);

            } else
                res = Processor.contrastProcessor(originalBufferedImage);

            try {
                imageCanvas.setOriginalBufferedImage(res);
                imageCanvas.repaint();
                System.out.println("Saving as testRes.png...");
                ImageIO.write(res, "png", new File("testRes.png"));
                System.out.println("Done.");

            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }

    }

    public void close() {

    }


    private class PointRep extends JComponent {

        int x;
        int y;

        PointRep(int x, int y) {
            this.x = x;
            this.y = y;
            setBounds(x - 10, y - 10, 20, 20);
        }

        public boolean collis(PointRep pointRep) {
            return ((pointRep.x - this.x) * (pointRep.x - this.x) + (pointRep.y - this.y) * (pointRep.y - this.y)) < 100;
        }

        boolean collis(int x, int y) {
            return ((x - this.x) * (x - this.x) + (y - this.y) * (y - this.y)) < 100;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            System.out.println("drawing");
            g.setColor(Color.GREEN);
            g.fillOval(0, 0, 20, 20);
        }
    }
}
