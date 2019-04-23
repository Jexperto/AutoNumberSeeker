package ImageProcessor;

import javafx.collections.ObservableList;
import net.sourceforge.tess4j.ITesseract;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

public class ProcessorManager implements Runnable {

    private ObservableList<ImageData> imageData;
    private CallbackProcessor stopSignal;
    private int countRunThreads;
    private int currentThreads;

    private ITesseract tesseract;
    private String platesDetectorName;

    public ProcessorManager(ObservableList<ImageData> imageData, ITesseract tesseract, String classifierName, CallbackProcessor stopSignal) {
        this.imageData = imageData;
        this.stopSignal = stopSignal;
        this.tesseract = tesseract;
        this.platesDetectorName = classifierName;
        countRunThreads = 0;
    }

    @Override
    public void run() {
        countRunThreads = imageData.size();
        currentThreads = 0;
        int index = 0;
        while (countRunThreads != 0) {
            if (currentThreads < 4 && index < imageData.size()) {
                if (imageData.get(index).getImageState() > 1) {
                    countRunThreads--;
                } else {
                    ProcessorTask task = new ProcessorTask(imageData.get(index));
                    if (task.start())
                        currentThreads++;
                    else
                        countRunThreads--;
                    imageData.get(index).requestSetImageState((byte) 1);
                }
                index++;
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignored) {
            }
        }
        stopSignal.stopped();
        System.out.println("stopped");
    }

    public boolean start() {
        if (imageData == null || stopSignal == null || imageData.isEmpty())
            return false;
        Thread thread = new Thread(this, "Manager of Processor");
        thread.start();
        return true;
    }

    public interface CallbackProcessor {
        void stopped();
    }

    private class ProcessorTask implements Runnable {

        private ImageData imageData;

        ProcessorTask(ImageData imageData) {
            this.imageData = imageData;
        }


        @Override
        public void run() {
            CascadeClassifier platesDetector = new CascadeClassifier();
            platesDetector.load(platesDetectorName);
            MatOfRect plates = new MatOfRect();
            int absolutePlateSize = 0;
            Mat matImage = Imgcodecs.imread(imageData.getImageFile().getAbsolutePath());
            Mat grayFrame = new Mat();
            Imgproc.cvtColor(matImage, grayFrame, Imgproc.COLOR_BGR2GRAY);
            Imgproc.equalizeHist(grayFrame, grayFrame);
            int height = grayFrame.rows();
            if (Math.round(height * 0.05f) > 0) {
                absolutePlateSize = Math.round(height * 0.05f);
            }
            //platesDetector.detectMultiScale(matImage, plates);
            platesDetector.detectMultiScale(grayFrame, plates, 1.1, 6, Objdetect.CASCADE_SCALE_IMAGE, new org.opencv.core.Size(absolutePlateSize,absolutePlateSize));
            //platesDetector.detectMultiScale(matImage, plates, 1.1, 2, Objdetect.CASCADE_SCALE_IMAGE, new Size(absolutePlateSize, absolutePlateSize), new Size());
            Rect[] plateRects = plates.toArray();
            int h = plateRects.length;
            if (h > 0) {
                imageData.setRectangleSet(plateRects);
                imageData.requestSetImageState((byte) 2);
            } else {
                System.out.println("ничего нету на картинке :(");
                imageData.requestSetImageState((byte) 3);
            }
            countRunThreads--;
            currentThreads--;
            //}
        }

        boolean start() {
            if (imageData == null)
                return false;
            Thread thread = new Thread(this, "Processor on " + imageData.getImageFile().getName());
            thread.start();
            return true;
        }
    }
}


/*try {
                BufferedImage image;
                image = ImageIO.read(imageData.getImageFile());
                ImageIO.write(Processor.testMethod2(image), "jpg", new File(imageData.getImageFile().getName()));
                System.out.println("test2 done");
                //BufferedImage res = Processor.binaryProcessor(image);
                int[][] data = Processor.conversionProcessor(image);
                new Processor.BinaryThreadManager(data, data2 -> {
                    next = true;
                });
                while (!next) {
                    Thread.sleep(1);
                }
                System.out.println("binary done");
                BufferedImage res = new BufferedImage(data[0].length, data.length, BufferedImage.TYPE_INT_RGB);
                for (int i = 0; i < data.length; i++) {
                    for (int j = 0; j < data[0].length; j++) {
                        res.setRGB(j, i, data[i][j]);
                    }
                }

                //res = Processor.repairProcessor(res);
                System.out.println("repair done");
                //res = Processor.linearProcessor(res);
                System.out.println("linear done");
                String result = " ";//tesseract.doOCR(res).trim().replace(" ", "");
                //System.out.println(result);
                if (result.isEmpty())
                    imageData.requestSetImageState((byte) 3);
                else {
                    imageData.requestSetImageState((byte) 2);
                    imageData.setNumber(result);
                }

            } catch (Exception e) {
                e.printStackTrace();
                imageData.requestSetImageState((byte) 3);
            } finally {*/