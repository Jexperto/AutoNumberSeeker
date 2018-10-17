package GUI;

import ImageProcessor.Processor;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TooManyListenersException;

public class ControlsPanel implements ActionListener {

    private Button openFileButton;
    private Button runButton;
    private JTextField filePathField;
    private boolean textFieldEntered;
    private ImageCanvas imageCanvas;
    private BufferedImage originalBufferedImage;
    private BufferedImage subBufferedImage;
    private Main mainFrame;
    private PointRep pointRep1;
    private PointRep pointRep2;
    private ITesseract tesseract;
    private File lastFilePath;

    //Инициализация управляющей панели
    ControlsPanel(Main main) {
        openFileButton = new Button("Открыть файл...");
        openFileButton.setBounds(25, 525, 100, 25);
        openFileButton.addActionListener(this);
        runButton = new Button("Распознать");
        runButton.setBounds(540, 525, 100, 25);
        runButton.addActionListener(this);
        filePathField = new JTextField("Путь к файлу...");
        textFieldEntered = false;
        filePathField.setEditable(false);
        filePathField.setBounds(130, 525, 400, 25);
        filePathField.addActionListener(this);
        filePathField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                textFieldEntered = true;
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                textFieldEntered = false;
            }

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                if (textFieldEntered) {
                    if (filePathField.getText().equals("Путь к файлу..."))
                        filePathField.setText("");
                    filePathField.setEditable(true);
                }
            }
        });
        imageCanvas = new ImageCanvas();

        imageCanvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                if (originalBufferedImage == null)
                    return;
                if (e.getX() > originalBufferedImage.getWidth() || e.getY() > originalBufferedImage.getHeight())
                    return;

                if (e.getButton() == 1 || e.getButton() == 3) {
                    if (pointRep1 == null) {
                        pointRep1 = new PointRep(e.getX(), e.getY());
                        imageCanvas.add(pointRep1);
                    } else if (pointRep2 == null && !pointRep1.isCollision(e.getX(), e.getY())) {
                        pointRep2 = new PointRep(e.getX(), e.getY());
                        imageCanvas.add(pointRep2);
                    } else if (pointRep1 != null && pointRep1.isCollision(e.getX(), e.getY())) {
                        imageCanvas.remove(pointRep1);
                        pointRep1 = null;
                    } else if (pointRep2 != null && pointRep2.isCollision(e.getX(), e.getY())) {
                        imageCanvas.remove(pointRep2);
                        pointRep2 = null;
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
        });

        lastFilePath = new File(".");

        mainFrame = main;

        mainFrame.add(openFileButton);
        mainFrame.add(filePathField);
        mainFrame.add(runButton);
        mainFrame.add(imageCanvas);

        mainFrame.setDropTarget(new DropTarget());
        mainFrame.getDropTarget().setActive(true);
        try {
            mainFrame.getDropTarget().addDropTargetListener(new DropTargetAdapter() {
                @SuppressWarnings("unchecked")
                @Override
                public void drop(DropTargetDropEvent dtde) {

                    Transferable transferable = dtde.getTransferable();

                    if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                        try {

                            Object object = transferable.getTransferData(DataFlavor.javaFileListFlavor);

                            ArrayList<File> files = (ArrayList<File>) object;

                            filePathField.setText(files.get(0).getName());
                            filePathField.setActionCommand(files.get(0).getName());

                        } catch (UnsupportedFlavorException | IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            });
        } catch (TooManyListenersException ignored) {

        }
        mainFrame.setTransferHandler(new TransferHandler(null) {
            @SuppressWarnings("unchecked")
            @Override
            public boolean importData(TransferSupport support) {


                Transferable transferable = support.getTransferable();

                if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    try {

                        Object object = transferable.getTransferData(DataFlavor.javaFileListFlavor);

                        ArrayList<File> files = (ArrayList<File>) object;

                        filePathField.setText(files.get(0).getName());
                        filePathField.setActionCommand(files.get(0).getName());

                    } catch (UnsupportedFlavorException | IOException e) {
                        e.printStackTrace();
                    }
                }

                return super.importData(support);
            }
        });


        tesseract = new Tesseract();
        tesseract.setLanguage("leu");
        tesseract.setDatapath(".");
        tesseract.setTessVariable("tessedit_char_whitelist", "acekopxyABCEHKMOPTXYD0123456789");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == openFileButton) {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Изображение(PNG, JPG, JPEG, GIF)", "png", "jpg", "jpeg", "gif");
            fileChooser.addChoosableFileFilter(filter);
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setCurrentDirectory(lastFilePath);

            switch (fileChooser.showOpenDialog(mainFrame)) {
                case JFileChooser.APPROVE_OPTION:
                    File file = fileChooser.getSelectedFile();
                    lastFilePath = new File(file.getAbsolutePath().replace(file.getName(), ""));
                    filePathField.setText(file.getPath());
                    filePathField.setEditable(false);
                    try {
                        if (pointRep1 != null) {
                            imageCanvas.remove(pointRep1);
                            pointRep1 = null;
                        }
                        if (pointRep2 != null) {
                            imageCanvas.remove(pointRep2);
                            pointRep2 = null;
                        }
                        originalBufferedImage = ImageIO.read(file);
                        originalBufferedImage = imageCanvas.scaleImage(originalBufferedImage);
                        imageCanvas.setOriginalBufferedImage(originalBufferedImage);
                        imageCanvas.repaint();
                    } catch (IOException e1) {
                        showDialog("Ошибка загрузки изображения", e1.getMessage());
                        imageCanvas.setOriginalBufferedImage(null);
                        imageCanvas.repaint();
                    }
                    break;
                case JFileChooser.CANCEL_OPTION:
                    break;
            }
        }
        if (e.getSource() == runButton) {

            if (originalBufferedImage == null) {
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

                imageCanvas.setOriginalBufferedImage(originalBufferedImage);
                imageCanvas.repaint();

                res = originalBufferedImage.getSubimage(x, y, w, h);
            } else
                res = originalBufferedImage;


            int splitW = res.getWidth() > 100 ? 100 : res.getWidth();
            int splitH = res.getHeight() > 100 ? 100 : res.getHeight();

            for (int i = 0; i <= res.getWidth() / splitW; i++) {
                for (int j = 0; j <= res.getHeight() / splitH; j++) {
                    Thread thread = new Thread(new Processor.ContrastThread(i * splitW, (i + 1) * splitW, j * splitH, (j + 1) * splitH, res));
                    thread.start();
                }
            }
            while (!Processor.ContrastThread.threadsStoped()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
            }

            try {
                ImageIO.write(res, "jpeg", new File("temp.jpg"));
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            BufferedImage res2 = Processor.linearProcessor(res);

            try {

                String result = tesseract.doOCR(res2).trim().replace(" ", "");

                if (result.replace("\n", "").isEmpty()) {
                    res = Processor.reversProcessor(res);
                    res = Processor.linearProcessor(res);
                    result = tesseract.doOCR(res).trim().replace(" ", "");
                }

                System.out.println(result);
                showDialog("Результат распознавания", result);

                System.out.println("Done.");

            } catch (TesseractException e1) {
                showDialog("Странная ошибка", e1.getMessage());
            }

        }
        if (e.getSource() == filePathField) {
            File file = new File(e.getActionCommand());
            try {
                originalBufferedImage = ImageIO.read(file);
                originalBufferedImage = imageCanvas.scaleImage(originalBufferedImage);
                imageCanvas.setOriginalBufferedImage(originalBufferedImage);
                imageCanvas.repaint();
                filePathField.setEditable(false);
                lastFilePath = new File(file.getAbsolutePath().replace(file.getName(), ""));
            } catch (IOException e1) {
                showDialog("Ошибка загрузки изображения", e1.getMessage());
            }
        }

    }

    private void showDialog(String title, String errorMessage) {

        JDialog dialog = new JDialog(mainFrame, title, JDialog.DEFAULT_MODALITY_TYPE);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        JTextField textArea = new JTextField(errorMessage);
        textArea.setEditable(false);
        dialog.setBounds(0, 0, 250, 100);
        dialog.setLocationRelativeTo(null);
        dialog.setLayout(new BorderLayout());
        dialog.add(textArea, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    void close() {

    }

    private class PointRep extends JComponent {

        private int x;
        private int y;

        PointRep(int x, int y) {
            this.x = x;
            this.y = y;
            setBounds(x - 5, y - 5, 10, 10);
        }

        boolean isCollision(int x, int y) {
            return ((x - this.x) * (x - this.x) + (y - this.y) * (y - this.y)) < 25;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.GREEN);
            g.fillOval(0, 0, 10, 10);
        }
    }
}
