//import ImageProcessor.Processor;
//
//import javax.imageio.ImageIO;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.IOException;
//
//public class Main {
//    // TODO: 18.05.2018 Сделать всё.
//
//    public static void main(String[] args) throws IOException {
//        for (int i = 1; i < 8; i++) {
//            BufferedImage bufferedImage = ImageIO.read(new File("test" + i + ".jpg"));
//            System.out.println("doing_" + i + "...");
//            if (bufferedImage == null)
//                continue;
//            Processor processor = new Processor(bufferedImage);
//            BufferedImage res = processor.testing();
//            ImageIO.write(res, "png", new File("testRes" + i + ".png"));
//        }
//    }
//
//
//}