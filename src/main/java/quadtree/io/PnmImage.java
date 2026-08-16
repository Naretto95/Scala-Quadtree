package quadtree.io;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Reader/writer for the plain-text Netpbm image formats: PBM (P1, black &amp; white),
 * PGM (P2, grayscale) and PPM (P3, RGB color).
 */
public final class PnmImage {

    /** RGB pixel data, indexed [row][col][channel]. Populated for PPM (P3) images. */
    public int[][][] rgbPixels;

    /** Single-channel pixel data, indexed [row][col]. Populated for PGM (P2) / PBM (P1) images. */
    public int[][] grayscalePixels;

    public int depth;
    public int width;
    public int height;

    public PnmImage() {
    }

    public PnmImage(int depth, int width, int height) {
        this.depth = depth;
        this.width = width;
        this.height = height;
        this.rgbPixels = new int[height][width][3];
        this.grayscalePixels = new int[height][width];
    }

    public void setRgbPixels(int[][][] pixels) {
        this.rgbPixels = pixels;
        this.height = pixels.length;
        this.width = pixels[0].length;
    }

    public void setGrayscalePixels(int[][] pixels) {
        this.grayscalePixels = pixels;
        this.height = pixels.length;
        this.width = pixels[0].length;
    }

    /**
     * Writes the image to disk. The output format (P1/P2/P3) is chosen from the file
     * extension: {@code .pbm}, {@code .pgm} or {@code .ppm}.
     */
    public void write(String fileName) {
        String[] parts = fileName.split("\\.");
        if (parts.length != 2) {
            System.out.println("File name must end in \".ppm\", \".pgm\" or \".pbm\"");
            return;
        }

        StringBuilder output = new StringBuilder();
        switch (parts[1]) {
            case "ppm":
                output.append("P3\n#\n").append(width).append(' ').append(height).append('\n').append(depth).append('\n');
                for (int[][] row : rgbPixels) {
                    for (int[] pixel : row) {
                        for (int channel : pixel) {
                            output.append(channel).append(' ');
                        }
                    }
                    output.append('\n');
                }
                break;
            case "pgm":
                output.append("P2\n#\n").append(width).append(' ').append(height).append('\n').append(depth).append('\n');
                appendGrayscale(output);
                break;
            case "pbm":
                output.append("P1\n#\n").append(width).append(' ').append(height).append('\n');
                appendGrayscale(output);
                break;
            default:
                System.out.println("Unsupported extension \"" + parts[1] + "\"; expected ppm, pgm or pbm");
                return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.append(output);
        } catch (IOException e) {
            System.out.println("Error while writing to " + fileName + ": " + e.getMessage());
        }
    }

    private void appendGrayscale(StringBuilder output) {
        for (int[] row : grayscalePixels) {
            for (int pixel : row) {
                output.append(pixel).append(' ');
            }
            output.append('\n');
        }
    }

    /** Reads a Netpbm image (P1, P2 or P3) from disk, auto-detecting the format from its header. */
    public void read(String fileName) {
        try (BufferedReader header = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
             DataInputStream body = new DataInputStream(new BufferedInputStream(new FileInputStream(fileName)))) {

            String magicNumber = header.readLine();
            body.skip((magicNumber + "\n").getBytes().length);

            // Skip comment lines before the dimensions.
            String line;
            do {
                line = header.readLine();
                body.skip((line + "\n").getBytes().length);
            } while (line.charAt(0) == '#');

            StringTokenizer dimensions = new StringTokenizer(line);
            width = Integer.parseInt(dimensions.nextToken());
            height = Integer.parseInt(dimensions.nextToken());

            if (magicNumber.equals("P2") || magicNumber.equals("P3")) {
                line = header.readLine();
                body.skip((line + "\n").getBytes().length);
                depth = Integer.parseInt(new StringTokenizer(line).nextToken());
            }

            try (Scanner pixels = new Scanner(body)) {
                if (magicNumber.equals("P3")) {
                    rgbPixels = new int[height][width][3];
                    for (int y = 0; y < height; y++) {
                        for (int x = 0; x < width; x++) {
                            for (int channel = 0; channel < 3; channel++) {
                                rgbPixels[y][x][channel] = pixels.nextInt();
                            }
                        }
                    }
                }

                if (magicNumber.equals("P1") || magicNumber.equals("P2")) {
                    grayscalePixels = new int[height][width];
                    for (int y = 0; y < height; y++) {
                        for (int x = 0; x < width; x++) {
                            grayscalePixels[y][x] = pixels.nextInt();
                        }
                    }
                    if (magicNumber.equals("P1")) {
                        depth = 1;
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Error: image in " + fileName + " is too large");
        } catch (FileNotFoundException e) {
            System.out.println("Error: file " + fileName + " not found");
        } catch (IOException e) {
            System.out.println("Error: unexpected end of stream while reading " + fileName);
        }
    }
}
