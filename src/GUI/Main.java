package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Main extends JFrame {

    public Main(String title) {
        super(title);
        ControlsPanel controlsPanel = new ControlsPanel(this);
        Dimension windowResolutionStandard = new Dimension(800, 600);
        this.setPreferredSize(windowResolutionStandard);
        this.setMaximumSize(windowResolutionStandard);
        this.setMinimumSize(windowResolutionStandard);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                controlsPanel.close();
                e.getWindow().dispose();
            }
        });
    }


    public static void main(String[] args) {
        initTessdata();
        Main main = new Main("Распознавание автомобильных номеров");
        main.setVisible(true);
    }


    private static void initTessdata() {

        File file = new File("tessdata");
        if (!file.exists() || !file.isDirectory())
            if (!file.mkdir())
                System.exit(-1);
        if (!(new File("tessdata\\leu.traineddata").exists())) {
            try {
                JarFile jarFile = new JarFile(new File("AutoNumberSeeker.jar"));
                JarEntry jarEntry = new JarEntry("tessdata/leu.traineddata");
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
