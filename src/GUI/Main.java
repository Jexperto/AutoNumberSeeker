package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

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
        Main main = new Main("Распознавание автомобильных номеров");
        main.setVisible(true);
    }


}
