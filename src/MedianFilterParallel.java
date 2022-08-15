import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Vector;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class MedianFilterParallel extends RecursiveAction {
    int HeightStart;
    int HeightEnd;
    static int windowWidth;
    static String inputImageName;
    static String outputImageName;
    static BufferedImage img = null;
    int SEQUENTIAL_CUTOFF = 150;

    public MedianFilterParallel(int Hstart, int Hend) {
        this.HeightStart = Hstart;
        this.HeightEnd = Hend;
    }

    @Override
    //responsible for executing the recursive action and if sequential cut of is met executes serially
    protected void compute() {
        if ((HeightEnd - HeightStart) < SEQUENTIAL_CUTOFF) {
            computeDirectly();
            return;
        }
        int middle = (HeightStart + HeightEnd) / 2;
        MedianFilterParallel Left = new MedianFilterParallel(HeightStart, middle + (windowWidth));
        MedianFilterParallel Right = new MedianFilterParallel(middle - (windowWidth), HeightEnd);
        invokeAll(Left, Right);

    }
//makes the pools for managing and running the threads and is responsible for setting priority to the threads
    public static void main(String[] args) {
        CaptureInput(args);
        ReadImage();
        MedianFilterParallel medianFilterParallel = new MedianFilterParallel(0, img.getHeight());
        ForkJoinPool forkjoinPool = new ForkJoinPool();
        long startTime = System.currentTimeMillis();
        forkjoinPool.invoke(medianFilterParallel);
        System.out.println((System.currentTimeMillis() - startTime) / 1000.F);
        WriteOutput();
    }
//executes the serial programme by applying the median filter
    protected void computeDirectly() {
        Vector<Integer> Red = new Vector<Integer>();
        Vector<Integer> Green = new Vector<Integer>();
        Vector<Integer> Blue = new Vector<Integer>();
        int squared = windowWidth * windowWidth;

        int pixel;
        for (int row = HeightStart; row < (HeightEnd - windowWidth); row++) {
            for (int col = 0; col < (img.getWidth() - windowWidth); col++) {
                int columncentre = (2 * row + windowWidth - 1) / 2;
                int rowcentre = (2 * col + windowWidth - 1) / 2;
                for (int subrow = row; subrow < (row + windowWidth); subrow++) {
                    for (int subcol = col; subcol < (col + windowWidth); subcol++) {
                        pixel = img.getRGB(subcol, subrow);
                        int r = (pixel >> 16) & 0xff;
                        int g = (pixel >> 8) & 0xff;
                        int b = pixel & 0xff;
                        Red.add(r);
                        Green.add(g);
                        Blue.add(b);

                    }
                }
                Collections.sort(Red);
                Collections.sort(Green);
                Collections.sort(Blue);
                int newpixel = (Red.get(squared / 2) << 16) | (Green.get(squared / 2) << 8) | Blue.get(squared / 2);
                img.setRGB(rowcentre, columncentre, newpixel);
                Red.clear();
                Green.clear();
                Blue.clear();
            }
        }
    }

//captures the command line arguments
    public static void CaptureInput(String[] args) {
        try {
            inputImageName = args[0];
            outputImageName = args[1];
            windowWidth = Integer.parseInt(args[2]);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("No input was provided");
        }
    }

//reads the image
    public static void ReadImage() {

        File f = null;

        try {
            f = new File(inputImageName);
            img = ImageIO.read(f);
        } catch (IOException e) {
            System.out.println("image not found!");
        }

    }
//outputs the image
    public static void WriteOutput() {
        try {
            // Output file path
            File output_file = new File(outputImageName);

            // Writing to file taking type and path as
            ImageIO.write(img, "jpg", output_file);

            System.out.println("Writing complete.");
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

    }
}
