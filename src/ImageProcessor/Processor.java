package ImageProcessor;

import java.awt.image.BufferedImage;

public class Processor {

    private static int lastBlackCount = 0;
    private static int lastWhiteCount = 0;

    //Контраст. Преобразует цветное изображение в чёрно-белый формат.
    public static BufferedImage contrastProcessor(BufferedImage origImage) {
        BufferedImage work = new BufferedImage(origImage.getWidth(), origImage.getHeight(), origImage.getType());
        lastBlackCount = 0;
        lastWhiteCount = 0;
        int width = work.getWidth();
        int height = work.getHeight();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int color = origImage.getRGB(i, j) & 0x00FFFFFF;

                int red = (color >>> 16) & 0xFF;
                int green = (color >>> 8) & 0xFF;
                int blue = color & 0xFF;

                if ((red > 50) && (Math.abs(red - green) < 40) && (Math.abs(red - blue) < 60) && (Math.abs(blue - green) < 40)) {
                    work.setRGB(i, j, 0x00FFFFFF);
                    lastWhiteCount++;
                    continue;
                }
                //схема цвета: 0xAARRGGBB, где AA - прозрачность (не учитывается), RR - красный, GG - зелёный, BB - синий.
                //Граничные условия определяют границу разделения цветов на черный и белый.
                //На данный момент условия закреплены, если для некоторых изображений будут осечки, придётся делать их динамичными.
                if (((color & 0x00ff0000) < 0x00f00000) && ((color & 0x0000ff00) < 0x0000a000) && ((color & 0x000000ff) < 0x000000f0)) {
                    work.setRGB(i, j, 0);
                    lastBlackCount++;
                } else {
                    work.setRGB(i, j, 0x00FFFFFF);
                    lastWhiteCount++;
                }
            }
        }
        return work;
    }

    //Инферсия изображения: черный -> белый, белый -> черный. Работает только после контраста. На данный момент отключён за неэффективностью.
    public static BufferedImage reversProcessor(BufferedImage origImage) {

        if (lastBlackCount > lastWhiteCount) {

            BufferedImage work = new BufferedImage(origImage.getWidth(), origImage.getHeight(), origImage.getType());

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

        return origImage;
    }

    //Линейный процессор (название осталось от первой попытки), обрабатывает изображение и удаляет всё, что не похоже на символы.
    public static BufferedImage linearProcessor(BufferedImage origImage) {

        BufferedImage work = new BufferedImage(origImage.getWidth(), origImage.getHeight(), origImage.getType());
        work.setData(origImage.getData());

        int width = work.getWidth();
        int height = work.getHeight();

        //Проверенные точки
        boolean[][] checkedPoint = new boolean[height][width];

        boolean repeat;

        //восстановление "битых" пикселей
        do {
            repeat = false;
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
                            repeat = true;
                        }
                    }
                }
            }
        } while (repeat);

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
