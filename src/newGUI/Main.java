package newGUI;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main extends JFrame {


    private Main() throws HeadlessException, IOException {
        super("Распознование автомобильных номеров");


        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(new FXPanel().createPanel());
        pack();

        List<BufferedImage> icons = new ArrayList<>();
        icons.add(ImageIO.read(getClass().getResource("../assets/icons/16.png")));
        icons.add(ImageIO.read(getClass().getResource("../assets/icons/32.png")));
        icons.add(ImageIO.read(getClass().getResource("../assets/icons/64.png")));
        icons.add(ImageIO.read(getClass().getResource("../assets/icons/128.png")));
        setIconImages(icons);
        setLocationRelativeTo(null);
        setResizable(false);
    }


    public static void main(String[] args) {
        try {
            Main main = new Main();
            main.setVisible(true);
        } catch (IOException e) {
            System.out.println("Sorry :(");
        }
    }
}
