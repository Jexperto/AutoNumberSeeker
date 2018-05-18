import ImageProcessor.Processor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    // TODO: 18.05.2018 Сделать всё.

    public static void main(String[] args) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new File("test.jpg"));

        System.out.println("doing...");
        if (bufferedImage == null)
            return;

        Processor processor = new Processor(bufferedImage);

        processor.testing();

    }


}


