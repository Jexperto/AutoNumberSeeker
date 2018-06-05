package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Main extends JFrame {

    private final Dimension WINDOW_RESOLUTION_STANDART = new Dimension(800, 600);


    public Main(String title) {
        super(title);
        ControlsPanel controlsPanel = new ControlsPanel(this);
        this.setPreferredSize(WINDOW_RESOLUTION_STANDART);
        this.setMaximumSize(WINDOW_RESOLUTION_STANDART);
        this.setMinimumSize(WINDOW_RESOLUTION_STANDART);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                controlsPanel.close();
                e.getWindow().dispose();

            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
    }


    public static void main(String[] args) {
        initTessdata();
        Main main = new Main("Распознавание автомобильных номеров");
        main.setVisible(true);
    }


    public static void initTessdata() {

        File file = new File("tessdata");
        if (!file.exists() || !file.isDirectory()) {
            file.mkdir();
            try {
                JarFile jarFile = new JarFile(new File("AutoNumberSeeker.jar"));
                JarEntry jarEntry = new JarEntry("tessdata/lau.traineddata");
                InputStream in = jarFile.getInputStream(jarEntry);
                FileOutputStream out = new FileOutputStream("tessdata\\leu.traineddata");
                int t;
                while ((t = in.read()) != -1) {
                    out.write(t);
                }
                in.close();
                out.close();
                jarFile.close();
            } catch (IOException e) {
                System.exit(-1);
            }
        }

    }

}
