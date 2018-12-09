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
import javafx.scene.shape.Rectangle;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;


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
    private CascadeClassifier platesDetector;
    private String platesDetectorName;
    private MatOfRect plates;
    private Rectangle rectangle;
    private int absolutePlateSize;
    private JFXPanel panel;

    @SuppressWarnings("unchecked")
    JFXPanel createPanel() throws IOException {
        panel = new JFXPanel();
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
            platesDetector = new CascadeClassifier();
            platesDetectorName = "C:\\Users\\zzhma\\IdeaProjects\\AutoNumberSeeker\\resourse\\cascades\\haar\\60_20_500_1000_14_4\\cascade.xml";
            System.out.println(platesDetector.load(platesDetectorName));
            plates = new MatOfRect();
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
                    mainImageView.setImage(imagesList.get(0).getWritableImage());
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
                    mainImageView.setImage(image.getWritableImage());
                    lastIndex = index;
                }
                if (running)
                    Platform.runLater(listenThread);
            };
            processorWorking = false;

//            proceedButton.setOnAction(event -> {
//            for (ImageData imageData:imagesList) {
//                BufferedImage image = null;
//                try {
//                    image = ImageIO.read(imageData.getImageFile());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                Mat matImage = Imgcodecs.imread(imageData.getImageFile().getAbsolutePath());
//                Mat grayFrame = new Mat();
//                Imgproc.cvtColor(matImage, grayFrame, Imgproc.COLOR_BGR2GRAY);
//                Imgproc.equalizeHist(grayFrame, grayFrame);
//                int height = matImage.rows();
//                if (Math.round(height * 0.2f) > 0)
//                    this.absolutePlateSize = Math.round(height * 0.2f);
//                //platesDetector.detectMultiScale(matImage, plates);
//                platesDetector.detectMultiScale(grayFrame, plates, 1.1, 4, Objdetect.CASCADE_SCALE_IMAGE, new Size(this.absolutePlateSize*3, this.absolutePlateSize), new Size());
//                Rect[] plateRects = plates.toArray();
//                int h = plateRects.length;
//                if(h > 0) {
//
//                    imageData.setRectangleSet(plateRects);
//                    //System.out.println(plateRects[0].toString());
//                }
//                else
//                    System.out.println("ничего нету на картинке :(");
//            }
//            });

            proceedButton.setOnAction(event -> {
                if (!processorWorking) {
                    ProcessorManager processorManager = new ProcessorManager(FXCollections.observableArrayList(imagesList), tesseract, platesDetectorName, () -> {
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
            System.out.println("CRITICAL ERROR: FAILED INITIALIZATION OF MAIN FRAME");
            System.exit(-1);
        }
        return panel;
    }

    void startListener() {
        running = true;
        Platform.runLater(listenThread);
    }

    //    void displayImage(ImageData imageData){
//        mainImageView.setImage(imageData.getWritableImage());
//        imageData.displayRect();
//    }
    void processImages() {

    }

    void stopListener() {
        running = false;
    }
}
