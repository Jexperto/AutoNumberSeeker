package GUI;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {

    private final Dimension WINDOW_RESOLUTION_STANDART = new Dimension(800, 600);


    public Main(String title) {
        super(title);
        ControlsPanel controlsPanel = new ControlsPanel(this);
        this.add(controlsPanel);
        this.setPreferredSize(WINDOW_RESOLUTION_STANDART);
        this.setMaximumSize(WINDOW_RESOLUTION_STANDART);
        this.setMinimumSize(WINDOW_RESOLUTION_STANDART);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
    }


    public static void main(String[] args) {
        Main main = new Main("Распознавание автомобильных номеров");
        main.setVisible(true);
    }


}
