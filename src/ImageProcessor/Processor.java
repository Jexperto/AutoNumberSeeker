package ImageProcessor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Processor {

    //Контраст. Преобразует цветное изображение в чёрно-белый формат.
    public static BufferedImage binaryProcessor(BufferedImage origImage) {
        BufferedImage work = new BufferedImage(origImage.getWidth(), origImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        int width = work.getWidth();
        int height = work.getHeight();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int color = origImage.getRGB(i, j) & 0x00FFFFFF;

                int red = (color >>> 16) & 0xFF;
                int green = (color >>> 8) & 0xFF;
                int blue = color & 0xFF;

                int level = 31;
                int line = 100;
                //Gray to white or black by split-line. Level - gray out step
                if ((red < line) && (Math.abs(red - green) < level) && (Math.abs(red - blue) < level) && (Math.abs(blue - green) < level)) {
                    work.setRGB(i, j, 0x00000000);
                    continue;
                } else if ((Math.abs(red - green) < level) && (Math.abs(red - blue) < level) && (Math.abs(blue - green) < level)) {
                    work.setRGB(i, j, 0x00FFFFFF);
                    continue;
                }
                //Yellow to white
                if ((Math.abs(red - green) < 80) && (Math.abs(red - blue) > 45) && (Math.abs(blue - green) > 45)) {
                    work.setRGB(i, j, 0x00FFFFFF);
                    continue;
                }
                //Red to black
                if ((Math.abs(red - green) > 80) && (Math.abs(red - blue) > 80) && (Math.abs(blue - green) < 30)) {
                    work.setRGB(i, j, 0x00000000);
                    continue;
                }
                //Blue to black
                if ((Math.abs(red - green) > 80) && (Math.abs(red - blue) > 80) && (Math.abs(blue - green) > 30)) {
                    work.setRGB(i, j, 0x00000000);
                    continue;
                }

                work.setRGB(i, j, 0x00000000);

                //схема цвета: 0xAARRGGBB, где AA - прозрачность (не учитывается), RR - красный, GG - зелёный, BB - синий.
                //Граничные условия определяют границу разделения цветов на черный и белый.
                //На данный момент условия закреплены, если для некоторых изображений будут осечки, придётся делать их динамичными.
            }
        }
        return work;
    }

    //Алгоритм Андрея, основанный на коде Фримана
    public static void testMethod2(BufferedImage origImage) {

        int height = origImage.getHeight();
        int width = origImage.getWidth();

        boolean[][] map = new boolean[height][width];
        boolean[][] checkMap = new boolean[height][width];
        int color;
        int red;
        int green;
        int blue;
        int red2;
        int green2;
        int blue2;

        boolean[][] checkCFC = new boolean[height][width];

        double level = 50;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (i > 0 && j > 0)
                    if (map[i - 1][j - 1])
                        if (i == 1 || map[i - 2][j - 1])
                            if (map[i][j - 1])
                                if (j == 1 || map[i - 1][j - 2])
                                    if (map[i - 1][j])
                                        checkCFC[i - 1][j - 1] = true;
                if (checkMap[i][j])
                    continue;
                color = origImage.getRGB(j, i) & 0x00FFFFFF;
                red = (color >>> 16) & 0xFF;
                green = (color >>> 8) & 0xFF;
                blue = color & 0xFF;
                map[i][j] = false;
                checkMap[i][j] = true;
                for (int k = 0; k < 4; k++) {
                    switch (k) {
                        case 0:
                            if (i > 0) {
                                color = origImage.getRGB(j, i - 1) & 0x00FFFFFF;
                            } else
                                continue;
                            break;
                        case 1:
                            if (i < height - 1) {
                                color = origImage.getRGB(j, i + 1) & 0x00FFFFFF;
                            } else
                                continue;
                            break;
                        case 2:
                            if (j > 0) {
                                color = origImage.getRGB(j - 1, i) & 0x00FFFFFF;
                            } else
                                continue;
                            break;
                        case 3:
                            if (j < width - 1) {
                                color = origImage.getRGB(j + 1, i) & 0x00FFFFFF;
                            } else
                                continue;
                            break;
                    }
                    red2 = (color >>> 16) & 0xFF;
                    green2 = (color >>> 8) & 0xFF;
                    blue2 = color & 0xFF;
                    if (Math.sqrt(Math.pow(red - red2, 2) + Math.pow(green - green2, 2) + Math.pow(blue - blue2, 2)) > level) {
                        map[i][j] = true;
                        switch (k) {
                            case 0:
                                checkMap[i - 1][j] = true;
                                break;
                            case 1:
                                checkMap[i + 1][j] = true;
                                break;
                            case 2:
                                checkMap[i][j - 1] = true;
                                break;
                            case 3:
                                checkMap[i][j + 1] = true;
                                break;
                        }
                        break;
                    }
                }
            }
        }

        for (int i = height - 2; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (!map[i][j])
                    continue;
                if (i > 0 && !map[i - 1][j])
                    continue;
                if (i < height - 1 && !map[i + 1][j])
                    continue;
                if (j > 0 && !map[i][j - 1])
                    continue;
                if (j < width - 1 && !map[i][j + 1])
                    continue;
                checkCFC[i][j] = true;
            }
        }

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (!map[i][j] || checkCFC[i][j])
                    continue;
                checkCFC[i][j] = true;
                StringBuilder builder = new StringBuilder();
                builder.append(i).append(':').append(j).append('=');
                boolean stop = false;
                int hStep = 0;
                int wStep = 0;
                int count = 1;
                do {
                    boolean ok = false;
                    for (int k = 0; k < 8; k++) {
                        switch (k) {
                            case 0:
                                if (i + hStep > 0 && !checkCFC[i - 1 + hStep][j + wStep] && map[i - 1 + hStep][j + wStep]) {
                                    checkCFC[i - 1 + hStep][j + wStep] = true;
                                    hStep--;
                                    builder.append(0).append('-');
                                    ok = true;
                                    count++;
                                }
                                break;
                            case 1:
                                if (i + hStep > 0 && j + wStep < width - 1 && !checkCFC[i - 1 + hStep][j + 1 + wStep] && map[i - 1 + hStep][j + 1 + wStep]) {
                                    checkCFC[i - 1 + hStep][j + 1 + wStep] = true;
                                    hStep--;
                                    wStep++;
                                    builder.append(1).append('-');
                                    ok = true;
                                    count++;
                                }
                                break;
                            case 2:
                                if (j + wStep < width - 1 && !checkCFC[i + hStep][j + 1 + wStep] && map[i + hStep][j + 1 + wStep]) {
                                    checkCFC[i + hStep][j + 1 + wStep] = true;
                                    wStep++;
                                    builder.append(2).append('-');
                                    ok = true;
                                    count++;
                                }
                                break;
                            case 3:
                                if (i + hStep < height - 1 && j + wStep < width - 1 && !checkCFC[i + 1 + hStep][j + 1 + wStep] && map[i + 1 + hStep][j + 1 + wStep]) {
                                    checkCFC[i + 1 + hStep][j + 1 + wStep] = true;
                                    hStep++;
                                    wStep++;
                                    builder.append(3).append('-');
                                    ok = true;
                                    count++;
                                }
                                break;
                            case 4:
                                if (i + hStep < height - 1 && !checkCFC[i + 1 + hStep][j + wStep] && map[i + 1 + hStep][j + wStep]) {
                                    checkCFC[i + 1 + hStep][j + wStep] = true;
                                    hStep++;
                                    builder.append(4).append('-');
                                    ok = true;
                                    count++;
                                }
                                break;
                            case 5:
                                if (i + hStep < height - 1 && j + wStep > 0 && !checkCFC[i + 1 + hStep][j - 1 + wStep] && map[i + 1 + hStep][j - 1 + wStep]) {
                                    checkCFC[i + 1 + hStep][j - 1 + wStep] = true;
                                    hStep++;
                                    wStep--;
                                    builder.append(5).append('-');
                                    ok = true;
                                    count++;
                                }
                                break;
                            case 6:
                                if (j + wStep > 0 && !checkCFC[i + hStep][j - 1 + wStep] && map[i + hStep][j - 1 + wStep]) {
                                    checkCFC[i + hStep][j - 1 + wStep] = true;
                                    wStep--;
                                    builder.append(6).append('-');
                                    ok = true;
                                    count++;
                                }
                                break;
                            case 7:
                                if (i + hStep > 0 && j + wStep > 0 && !checkCFC[i - 1 + hStep][j - 1 + wStep] && map[i - 1 + hStep][j - 1 + wStep]) {
                                    checkCFC[i - 1 + hStep][j - 1 + wStep] = true;
                                    hStep--;
                                    wStep--;
                                    builder.append(5).append('-');
                                    ok = true;
                                    count++;
                                }
                                break;
                        }
                        if (ok)
                            break;
                    }
                    if (!ok)
                        stop = true;
                } while (!stop);
                if (count > 80) {
                    //System.out.println(builder.toString());
                    // TODO: 24.10.2018 Здесь в builder'e лежит цепной код Фримена. Необходимо придумать механизм сравнения этого кода с некоторым шаблоном, для определения части или полного прямоугольника.
                    // Формат кода: i:j=0-0-0-0-0-....
                }
            }
        }

        //for tests
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                newImage.setRGB(j, i, map[i][j] ? 0 : 0x00FFFFFF);
            }
        }
        try {
            ImageIO.write(newImage, "jpeg", new File("w" + width + "_h" + height + "_test2.jpg"));
        } catch (IOException ignored) {
        }
    }

    //Инверсия изображения: черный -> белый, белый -> черный. Работает только после контраста.
    public static BufferedImage inversionProcessor(BufferedImage origImage) {
        BufferedImage work = new BufferedImage(origImage.getWidth(), origImage.getHeight(), BufferedImage.TYPE_INT_RGB);

        int width = work.getWidth();
        int height = work.getHeight();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {

                if ((origImage.getRGB(i, j) & 0x00FFFFFF) == 0x00FFFFFF)
                    work.setRGB(i, j, 0);
                else
                    work.setRGB(i, j, 0x00FFFFFF);
            }
        }

        return work;
    }

    //Восстановление "битых" пикселей
    public static BufferedImage repairProcessor(BufferedImage origImage) {
        BufferedImage work = new BufferedImage(origImage.getWidth(), origImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        work.setData(origImage.getData());
        int width = work.getWidth();
        int height = work.getHeight();
        //восстановление "битых" пикселей
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if ((work.getRGB(j, i) & 0x00FFFFFF) == 0x00FFFFFF) {
                    int count = 0;
                    if (i > 0 && (work.getRGB(j, i - 1) & 0x00FFFFFF) == 0)
                        count++;
                    if (i < height - 1 && (work.getRGB(j, i + 1) & 0x00FFFFFF) == 0)
                        count++;
                    if (j > 0 && (work.getRGB(j - 1, i) & 0x00FFFFFF) == 0)
                        count++;
                    if (j < width - 1 && (work.getRGB(j + 1, i) & 0x00FFFFFF) == 0)
                        count++;
                    if (count > 2) {
                        work.setRGB(j, i, 0x00000000);
                        i -= i == 0 ? 0 : 1;
                        j -= j == 0 ? 1 : 2;
                    }
                }
            }
        }
        return work;
    }

    //Линейный процессор (название осталось от первой попытки), обрабатывает изображение и удаляет всё, что не похоже на символы.
    public static BufferedImage linearProcessor(BufferedImage origImage) {

        BufferedImage work = new BufferedImage(origImage.getWidth(), origImage.getHeight(), origImage.getType());
        work.setData(origImage.getData());

        int width = work.getWidth();
        int height = work.getHeight();

        //Проверенные точки
        boolean[][] checkedPoint = new boolean[height][width];


        //Зонированная обработка
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (!checkedPoint[i][j] && (work.getRGB(j, i) & 0x00FFFFFF) == 0) {
                    checkedPoint[i][j] = true;

                    //Черные пиксели найденной зоны
                    boolean[][] ourBlack = new boolean[height][width];
                    ourBlack[i][j] = true;

                    //Нахождение пикселей, принадлежащих зоне.
                    int direction = 1;
                    int minH = i;
                    int maxH = i;
                    int minW = j;
                    int maxW = j;
                    int blackCount = 1;
                    boolean second = false;
                    //Обработка строки, где найден первй пиксель.
                    for (int l = j + 1; l < width; l += direction) {
                        if (!checkedPoint[i][l] && ((l > 0 && ourBlack[i][l - 1]) || (l < width - 1 && ourBlack[i][l + 1]))) {
                            if ((work.getRGB(l, i) & 0x00FFFFFF) == 0) {
                                ourBlack[i][l] = true;
                                minW = Math.min(minW, l);
                                maxW = Math.max(maxW, l);
                                blackCount++;
                                checkedPoint[i][l] = true;
                            }
                        }
                        if (!second && l == width - 1) {
                            direction = -1;
                            second = true;
                        } else if (second && l == 0) {
                            break;
                        }
                    }
                    boolean has = false;
                    boolean has2 = false;
                    boolean second2 = false;
                    int direction2 = 1;

                    //Поиск других строк
                    for (int k = i + 1; k < height; k += direction2) {
                        second = false;
                        direction = 1;
                        for (int l = 0; l < width; l += direction) {

                            if (!checkedPoint[k][l] && ((l > 0 && ourBlack[k][l - 1]) || (l < width - 1 && ourBlack[k][l + 1]) || (k > 0 && ourBlack[k - 1][l]) || (l > 0 && k > 0 && ourBlack[k - 1][l - 1]) || (l < width - 1 && k > 0 && ourBlack[k - 1][l + 1]) || (k < height - 1 && ourBlack[k + 1][l]) || (l > 0 && k < height - 1 && ourBlack[k + 1][l - 1]) || (l < width - 1 && k < height - 1 && ourBlack[k + 1][l + 1]))) {
                                if ((work.getRGB(l, k) & 0x00FFFFFF) == 0) {
                                    ourBlack[k][l] = true;
                                    minW = Math.min(minW, l);
                                    maxW = Math.max(maxW, l);
                                    maxH = Math.max(maxH, k);
                                    minH = Math.min(minH, k);
                                    has = true;
                                    has2 = true;
                                    blackCount++;
                                    checkedPoint[k][l] = true;
                                }
                            }
                            if (!second && l == width - 1) {
                                direction = -1;
                                second = true;
                                has = false;
                            } else if (second && l == 0) {
                                if (has) {
                                    second = false;
                                    direction = 1;
                                    has = false;
                                } else
                                    break;
                            }
                        }
                        if (!second2 && k == height - 1) {
                            direction2 = -1;
                            second2 = true;
                            has2 = false;
                        } else if (second2 && k == 0) {
                            if (has2) {
                                second2 = false;
                                direction2 = 1;
                                has2 = false;
                            } else
                                break;
                        }
                    }

                    //Вычисление размеров зоны
                    int lenW = maxW - minW + 1;
                    int lenH = maxH - minH + 1;
                    //Вычисления параметров соотношения
                    int len = lenH - lenW;
                    int lenPercent = (int) Math.round(((double) lenW / lenH) * 100);
                    int percent = (int) Math.round(((double) blackCount / (lenW * lenH)) * 100);


                    //Проверка на вертикальные границы
                    int count1 = 0;
                    int count2 = 0;
                    has = false;
                    has2 = false;
                    boolean left = false;
                    boolean right = false;
                    for (int k = minH; k <= maxH; k++) {
                        if (ourBlack[k][minW] || (minW < width - 1 && ourBlack[k][minW + 1])) {
                            if (!left) {
                                count1++;
                                has = true;
                            }
                        } else if (has)
                            left = true;
                        if (ourBlack[k][maxW] || (maxW > 0 && ourBlack[k][maxW - 1])) {
                            if (!right) {
                                count2++;
                                has2 = true;
                            }
                        } else if (has2)
                            right = true;
                        if (left && right)
                            break;
                    }
                    left = false;
                    right = false;
                    if (lenH - count1 <= lenH / 4)
                        left = true;
                    if (lenH - count2 <= lenH / 4)
                        right = true;

                    /*if (right && !left)
                        if (count1 > lenH / 4)
                            left = true;*/
                    if (!right && left)
                        if (count2 > lenH / 4)
                            right = true;

                    /*if (count1 > lenH / 4)
                        left = true;
                    if (count2 > lenH / 4)
                        right = true;*/

                    //Проверка на горизонтальные границы
                    count1 = 0;
                    count2 = 0;
                    has = false;
                    has2 = false;
                    boolean up = false;
                    boolean down = false;
                    for (int k = minW; k <= maxW; k++) {
                        if (ourBlack[minH][k] || (minH < width - 1 && ourBlack[minH + 1][k])) {
                            if (!up) {
                                count1++;
                                has = true;
                            }
                        } else if (has)
                            up = true;
                        if (ourBlack[maxH][k] || (maxH > 0 && ourBlack[maxH - 1][k])) {
                            if (!down) {
                                count2++;
                                has2 = true;
                            }
                        } else if (has2) {
                            down = true;
                        }
                        if (up && down)
                            break;
                    }
                    up = false;
                    down = false;
                    if (lenW - count1 <= lenW / 4)
                        up = true;
                    if (lenW - count2 <= lenW / 4)
                        down = true;
                    if (left) {
                        if (count1 > lenW / 4)
                            up = true;
                        if (count2 > lenW / 4)
                            down = true;
                    }


                    //Если это не угловой элемент, то проверить параметры соотношений, иначе просто стереть объект
                    if (!(up && left && !down && !right) && !(up && !left && !down && right) && !(!up && left && down && !right) && !(!up && !left && down && right))
                        if ((len > 0 && lenPercent > 30 && lenPercent < 87 && percent > 20 && percent < 67))
                            continue;

                    //Удаление (обесчвечивание) черного цвета зоны
                    for (int k = minH; k <= maxH; k++) {
                        for (int l = minW; l <= maxW; l++) {
                            if (ourBlack[k][l])
                                work.setRGB(l, k, 0x00FFFFFF);
                        }
                    }
                } else {
                    checkedPoint[i][j] = true;
                }
            }
        }
        return work;
    }

}
