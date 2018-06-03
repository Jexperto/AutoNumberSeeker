package GUI;

import ImageProcessor.Processor;

import javax.imageio.ImageIO;
import javax.naming.ldap.Control;
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
    JFrame mainFrame;

    ControlsPanel() throws IOException {
        openFileButton = new Button("Открыть файл...");
        openFileButton.setBounds(25,525,100,25);
        openFileButton.addActionListener(this);
        runButton = new Button("Тестировать");
        runButton.setBounds(540,525,100,25);
        runButton.addActionListener(this);
        filePathField = new TextField("Путь к файлу...");
        filePathField.setEditable(false);
        filePathField.setBounds(130,525,400,25);
        fc = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Images(.png, .jpg, .jpeg, .gif)", "png", "jpg","jpeg", "gif");
        fc.addChoosableFileFilter(filter);
        fc.setAcceptAllFileFilterUsed(false);

        File currentDirFile = new File(".");
        String helper = currentDirFile.getAbsolutePath();
        String currentDir = helper.substring(0, helper.length() - currentDirFile.getCanonicalPath().length());



        fc.setCurrentDirectory(new File("."));
        Main.mainWindow.add(openFileButton);
        Main.mainWindow.add(filePathField);
        Main.mainWindow.add(runButton);
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
                    Main.mainWindow.add(imageCanvas);
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
                    Main.mainWindow.add(imageCanvas);
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
