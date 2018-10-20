package newGUI;


import javafx.collections.FXCollections;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Main extends JFrame {


    private Main() throws HeadlessException {
        super("Распознование автомобильных номеров");


            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setContentPane(FXPanel.createPanel());
            pack();
            setLocationRelativeTo(null);
            setResizable(false);
        }



    public static void main(String[] args) {
        Main main = new Main();
        main.setVisible(true);
    }
}
