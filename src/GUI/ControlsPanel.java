package GUI;

import ImageProcessor.Processor;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ControlsPanel implements ActionListener {

    private Button openFileButton;
    private Button runButton;
    private TextField filePathField;
    private ImageCanvas imageCanvas;
    private BufferedImage originalBufferedImage;
    private Main mainFrame;

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
            Processor processor = new Processor(originalBufferedImage);
            BufferedImage res = processor.testing();
            try {
                imageCanvas.setOriginalBufferedImage(res);
                imageCanvas.repaint();
                System.out.println("Saving as testres.png...");
                ImageIO.write(res, "png", new File("testRes.png"));
                System.out.println("Done.");

            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }

    }

    public void close() {

    }
}
