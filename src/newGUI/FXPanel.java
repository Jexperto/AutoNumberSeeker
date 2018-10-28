package newGUI;

import ImageProcessor.ImageData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;


public class FXPanel {

    int lastIndex;

    @SuppressWarnings("unchecked")
    JFXPanel createPanel() {
        JFXPanel panel = new JFXPanel();
        try {
            ObservableList<ImageData> imagesList = FXCollections.observableArrayList();
            ObservableList<BorderPane> borderList = FXCollections.observableArrayList();
            Parent parent = FXMLLoader.load(FXPanel.class.getResource("../assets/fxmls/newGUI.fxml"));
            panel.setScene(new Scene(parent));
            ListView<BorderPane> listView = (ListView<BorderPane>) parent.lookup("#imageList");
            Button loadButton = (Button) parent.lookup("#loadButton");
            Button proceedButton = (Button) parent.lookup("#proceedButton");
            ImageView mainImageView = (ImageView) parent.lookup("#mainImage");
            lastIndex = 0;

            listView.setItems(borderList);

            //loadButton
            loadButton.setOnAction(event -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Resource File");
                fileChooser.setInitialDirectory(new File("."));
                List<File> imageFilesList = fileChooser.showOpenMultipleDialog(null);
                if (imageFilesList == null) return;
                for (File file : imageFilesList) {
                    ImageData image = new ImageData(file);
                    imagesList.add(image);
                    borderList.add(image.createBorderPane());
                }
            });


            // Обработка отображения картинки по
            listView.setOnMousePressed(event -> {
                int index = listView.getSelectionModel().getSelectedIndex();
                if (index != -1) {
                    imagesList.get(lastIndex).setDefaultColor();
                    ImageData image = imagesList.get(index);
                    image.setWhiteColor();
                    mainImageView.setImage(new Image("file:///" + image.getImageFile().getAbsolutePath()));
                    lastIndex = index;
                }
            });


            //

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("CRITICAL ERROR: FAIL INIT MAIN FRAME");
            System.exit(-1);
        }
        return panel;
    }
}
