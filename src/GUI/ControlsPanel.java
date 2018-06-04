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

public class ControlsPanel extends JPanel implements ActionListener {

    Button openFileButton;
    Button runButton;
    TextField filePathField;
    File file;
    BufferedImage originalBufferedImage;
    final JFileChooser fc;
    Main mainFrame;

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
        fc = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Images(.png, .jpg, .jpeg, .gif)", "png", "jpg", "jpeg", "gif");
        fc.addChoosableFileFilter(filter);
        fc.setAcceptAllFileFilterUsed(false);
        mainFrame = main;

        File currentDirFile = new File(".");
        String helper = currentDirFile.getAbsolutePath();
        try {
            String currentDir = helper.substring(0, helper.length() - currentDirFile.getCanonicalPath().length());
        } catch (IOException e) {
            e.printStackTrace();
        }


        fc.setCurrentDirectory(new File("."));
        mainFrame.add(openFileButton);
        mainFrame.add(filePathField);
        mainFrame.add(runButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //Handle open button action.
        if (e.getSource() == openFileButton) {
            int returnVal = fc.showOpenDialog(ControlsPanel.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                file = fc.getSelectedFile();
                filePathField.setText(file.getPath());
                try {
                    originalBufferedImage = ImageIO.read(file);
                    ImageCanvas imageCanvas = new ImageCanvas(originalBufferedImage);
                    mainFrame.add(imageCanvas);
                    imageCanvas.repaint();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            } else {
                System.out.println("Open command cancelled by user.\n");
            }
        }
        if (e.getSource() == runButton) {

            System.out.println("doing...");
            if (originalBufferedImage == null)
                return;
            Processor processor = new Processor(originalBufferedImage);
            BufferedImage res = processor.testing();
            try {
                ImageCanvas imageCanvas = new ImageCanvas(res);
                mainFrame.add(imageCanvas);
                imageCanvas.repaint();
                System.out.println("Saving as testres.png...");
                ImageIO.write(res, "png", new File("testRes.png"));
                System.out.println("Done.");

            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }
    }


    public BufferedImage getOriginalBufferedImage() {
        return originalBufferedImage;
    }
}
