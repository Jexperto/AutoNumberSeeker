package newGUI;

import ImageProcessor.ImageData;
import ImageProcessor.ProcessorManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;


class FXPanel {

    private int lastIndex;
    private ListView<BorderPane> listView;
    private Runnable listenThread;
    private ObservableList<ImageData> imagesList;
    private ImageView mainImageView;
    private boolean running;
    private String lastDirectory;
    private boolean processorWorking;
    private ITesseract tesseract;

    @SuppressWarnings("unchecked")
    JFXPanel createPanel() {
        JFXPanel panel = new JFXPanel();
        try {
            imagesList = FXCollections.observableArrayList();
            ObservableList<BorderPane> borderList = FXCollections.observableArrayList();
            Parent parent = FXMLLoader.load(FXPanel.class.getResource("../assets/fxmls/newGUI.fxml"));
            panel.setScene(new Scene(parent));
            listView = (ListView<BorderPane>) parent.lookup("#imageList");
            Button loadButton = (Button) parent.lookup("#loadButton");
            Button proceedButton = (Button) parent.lookup("#proceedButton");
            mainImageView = (ImageView) parent.lookup("#mainImage");
            lastIndex = -1;

            listView.setItems(borderList);

            //File chooser
            lastDirectory = ".";
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Загрузить изображения");
            fileChooser.setFileFilter(new FileNameExtensionFilter("image files", "png", "gif", "jpg", "jpeg", "bmp"));
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setMultiSelectionEnabled(true);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            //loadButton
            loadButton.setOnAction(event -> {
                fileChooser.setCurrentDirectory(new File(lastDirectory));
                File[] imageFilesList;
                switch (fileChooser.showOpenDialog(panel)) {
                    case JFileChooser.APPROVE_OPTION:
                        imageFilesList = fileChooser.getSelectedFiles();
                        break;
                    case JFileChooser.CANCEL_OPTION:
                        return;
                    case JFileChooser.ERROR_OPTION:
                        return;
                    default:
                        return;
                }
                lastDirectory = imageFilesList[0].getParent();
                for (File file : imageFilesList) {
                    boolean skip = false;
                    for (ImageData imageData : imagesList) {
                        if (imageData.getImageFile().getAbsolutePath().equals(file.getAbsolutePath())) {
                            skip = true;
                            break;
                        }
                    }
                    if (skip)
                        continue;
                    ImageData image = new ImageData(file);
                    imagesList.add(image);
                    borderList.add(image.createBorderPane());
                }
                if (lastIndex == -1) {
                    lastIndex = 0;
                    listView.getSelectionModel().select(0);
                    imagesList.get(0).setWhiteColor();
                    mainImageView.setImage(imagesList.get(0).getImage());
                }
                listView.requestFocus();
            });

            listenThread = () -> {
                int index = listView.getSelectionModel().getSelectedIndex();
                if (index != -1 && index != lastIndex) {
                    if (lastIndex != -1)
                        imagesList.get(lastIndex).setDefaultColor();
                    ImageData image = imagesList.get(index);
                    image.setWhiteColor();
                    mainImageView.setImage(image.getImage());
                    lastIndex = index;
                }
                if (running)
                    Platform.runLater(listenThread);
            };
            processorWorking = false;
            proceedButton.setOnAction(event -> {
                if (!processorWorking) {
                    ProcessorManager processorManager = new ProcessorManager(FXCollections.observableArrayList(imagesList), tesseract, () -> {
                        processorWorking = false;
                        panel.requestFocus();
                    });
                    processorWorking = processorManager.start();
                }
            });

            tesseract = new Tesseract();
            tesseract.setLanguage("leu");
            tesseract.setDatapath(".");
            tesseract.setTessVariable("tessedit_char_whitelist", "acekopxyABCEHKMOPTXYD0123456789");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("CRITICAL ERROR: FAIL INIT MAIN FRAME");
            System.exit(-1);
        }
        return panel;
    }

    void startListener() {
        running = true;
        Platform.runLater(listenThread);
    }

    void stopListener() {
        running = false;
    }
}
