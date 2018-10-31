package ImageProcessor;

import javafx.collections.ObservableList;
import net.sourceforge.tess4j.ITesseract;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class ProcessorManager implements Runnable {

    private ObservableList<ImageData> imageData;
    private CallbackProcessor stopSignal;
    private int countRunThreads;
    private int currentThreads;

    private ITesseract tesseract;

    public ProcessorManager(ObservableList<ImageData> imageData, ITesseract tesseract, CallbackProcessor stopSignal) {
        this.imageData = imageData;
        this.stopSignal = stopSignal;
        this.tesseract = tesseract;
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
                    index++;
                    continue;
                }
                ProcessorTask task = new ProcessorTask(imageData.get(index));
                if (task.start())
                    currentThreads++;
                else
                    countRunThreads--;
                imageData.get(index).requestSetImageState((byte) 1);
                index++;
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

        ImageData imageData;

        ProcessorTask(ImageData imageData) {
            this.imageData = imageData;
        }

        @Override
        public void run() {
            try {
                BufferedImage image;
                image = ImageIO.read(imageData.getImageFile());
                Processor.testMethod2(image);
                System.out.println("test2 done");
                BufferedImage res = Processor.binaryProcessor(image);
                System.out.println("binary done");
                BufferedImage res2 = Processor.repairProcessor(res);
                System.out.println("repair done");
                BufferedImage res3 = Processor.linearProcessor(res2);
                System.out.println("linear done");
                String result = tesseract.doOCR(res3).trim().replace(" ", "");
                System.out.println(result);
                if (result.isEmpty())
                    imageData.requestSetImageState((byte) 3);
                else {
                    imageData.requestSetImageState((byte) 2);
                    imageData.setNumber(result);
                }

            } catch (Exception e) {
                e.printStackTrace();
                imageData.requestSetImageState((byte) 3);
            } finally {
                countRunThreads--;
                currentThreads--;
            }
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
