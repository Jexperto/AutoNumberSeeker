package GUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {

    static JFrame mainWindow;


    private static final Dimension WINDOW_RESOLUTION = new Dimension(800,600);

    public static void main(String[] args) throws IOException {
        mainWindow = new JFrame("Распознавание автомобильных номеров");
        ControlsPanel controlsPanel = new ControlsPanel();
        mainWindow.add(controlsPanel);
        mainWindow.setPreferredSize(WINDOW_RESOLUTION);
        mainWindow.setMaximumSize(WINDOW_RESOLUTION);
        mainWindow.setMinimumSize(WINDOW_RESOLUTION);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setResizable(false);
        mainWindow.setLocationRelativeTo(null);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setVisible(true);
    }


}
