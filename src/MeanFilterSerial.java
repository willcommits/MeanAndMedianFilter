import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Vector;

public class MeanFilterSerial {
    static String inputImageName;
    static String outputImageName;
    static int windowWidth;
    static int ImageWidth;
    static int ImageHeight;

    static BufferedImage img = null;

//runs the program
    public static void main(String[] args) {
        CaptureInput(args);
        ProcessImageColors();
        WriteOutput();

    }

    /**
     * Creates a buffered Image and links to image on the PC
     * Extracts Pixels and extract each color pixels and appends to Array
     **/
    public static void ProcessImageColors() {

        File f = null;

        try {
            f = new File(inputImageName);
            img = ImageIO.read(f);
        } catch (IOException e) {
            System.out.println("image not found!");
        }
        MeanFilter();

    }

//executes the mean filter
    public static void MeanFilter() {
        int Red = 0;
        int Green = 0;
        int Blue = 0;
        int squared = windowWidth * windowWidth;
        int windowhalf = windowWidth / 2;
        int pixel;
        long startTime = System.currentTimeMillis();
        for (int row = 0; row < (img.getHeight() - windowWidth); row++) {
            int rowend = row + windowWidth;
            for (int col = 0; col < (img.getWidth() - windowWidth); col++) {
                int columncentre = (2 * row + windowWidth - 1) / 2;
                int rowcentre = (2 * col + windowWidth - 1) / 2;
                int colend = col + windowWidth;
                for (int subrow = row; subrow < (row + windowWidth); subrow++) {
                    for (int subcol = col; subcol < (col + windowWidth); subcol++) {
                        pixel = img.getRGB(subcol, subrow);
                        int r = (pixel >> 16) & 0xff;
                        int g = (pixel >> 8) & 0xff;
                        int b = pixel & 0xff;
                        Red += r;
                        Green += g;
                        Blue += b;

                    }
                }

                Red = Red / squared;
                Green = Green / squared;
                Blue = Blue / squared;
                int newpixel = 0;
                newpixel = (Red << 16) | (Green << 8) | Blue;
                img.setRGB(rowcentre, columncentre, newpixel);
                Red = 0;
                Green = 0;
                Blue = 0;
            }
        }
        System.out.println((System.currentTimeMillis() - startTime) / 1000.F);
    }

//writes the output of the image using the commandline output text it received
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

    // Command Line to Capture Input
    public static void CaptureInput(String[] args) {
        try {
            inputImageName = args[0];
            outputImageName = args[1];
            windowWidth = Integer.parseInt(args[2]);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("No input was provided");
        }
    }
}
