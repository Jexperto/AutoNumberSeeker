package newGUI;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main extends JFrame {

    private Main() throws HeadlessException, IOException {
        super("Распознование автомобильных номеров");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
            System.out.println("can't set system UI look and feel");
        }
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        FXPanel panel = new FXPanel();
        setContentPane(panel.createPanel());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                panel.stopListener();
                e.getWindow().dispose();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                System.exit(0);
            }

            @Override
            public void windowActivated(WindowEvent e) {
                super.windowActivated(e);
                e.getWindow().setIgnoreRepaint(false);
                panel.startListener();
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                super.windowDeactivated(e);
                panel.stopListener();
                e.getWindow().setIgnoreRepaint(true);
            }

        });
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
