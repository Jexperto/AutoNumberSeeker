package newGUI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;


public class FXPanel {


    @SuppressWarnings("unchecked")
    static JFXPanel createPanel() {
        JFXPanel panel = new JFXPanel();
        try {
            //System.out.println(getClass().getResource("newGUI/newGUI.fxml"));
            Parent parent = FXMLLoader.load(FXPanel.class.getResource("fxml/newGUI.fxml"));
            panel.setScene(new Scene(parent));
            ListView<String> listView = (ListView<String>) parent.lookup("#imageList");
            Button loadButton = (Button) parent.lookup("#loadButton");
            Button proceedButton = (Button) parent.lookup("#proceedButton");
            ImageView mainImage = (ImageView) parent.lookup("mainImage");

            //ListView
            ObservableList<String> imagesList = FXCollections.observableArrayList();
            imagesList.add("Image 1");
            imagesList.add("Image 2");
            imagesList.add("Image 3");
            imagesList.add("Image 4");
            imagesList.add("Image 5");
            listView.setItems(imagesList);



            //

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("CRITICAL ERROR: FAIL INIT MAIN FRAME");
            System.exit(-1);
        }
        return panel;
    }
}
