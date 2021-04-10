package ImageReaderWriterPackage;

import java.io.*;
import java.util.*;

public class ImageReaderWriter{

    public int [][][] pixelsP3;
    public int [][] pixelsP1P2;
    public int depth,width,height;

    public ImageReaderWriter(){
        depth = width = height = 0;
    }

    public ImageReaderWriter(int inDepth, int inWidth, int inHeight){
        pixelsP3 = new int[inHeight][inWidth][3];
        pixelsP1P2 = new int[inHeight][inWidth];
        width = inWidth;
        height = inHeight;
        depth = inDepth;
    }

    public void setPixelsP3(int[][][] matrix){
    	this.pixelsP3 = matrix;
    	this.width=matrix[0].length;
    	this.height=matrix.length;
    }

    public void setPixelsP1P2(int[][] matrix){
        this.pixelsP1P2 = matrix;
        this.width=matrix[0].length;
        this.height=matrix.length;
    }

    public void Write(String fileName){
        StringBuilder output = new StringBuilder();
        String fileNameCopy = new String(fileName);
        String[] test = fileNameCopy.split("\\.");
        if(test.length!=2){
            System.out.println("new file name should be as \"*.ppm\" or \"*.pgm\" or \"*.pbm\"");
            return;
        } 
       	String ext = test[1];
        switch(ext){
        	case "ppm":
        		output.append("P3\n#\n"+width+" "+height+"\n"+depth+"\n");
        		for (int[][] line : this.pixelsP3){
        			for(int[] pixel : line){
        				for (int value : pixel) {
        					output.append(value+" ");
        				}
        			}
        			output.append("\n");
        		}
        		break;
        	case "pgm":
                output.append("P2\n#\n"+width+" "+height+"\n"+depth+"\n");
                for (int[] line : this.pixelsP1P2){
                    for(int pixel : line){
                        output.append(pixel+" ");
                    }
                    output.append("\n");
                }
        		break;
        	case "pbm":
                output.append("P1\n#\n"+width+" "+height+"\n");
                for (int[] line : this.pixelsP1P2){
                    for(int pixel : line){
                        output.append(pixel+" ");
                    }
                    output.append("\n");
                }
        		break;
        	default:
        		break;
        }
         
        FileWriter fos = null;
        BufferedWriter dos = null;
 
        try {
             
            fos = new FileWriter(fileName);
             
            dos = new BufferedWriter(fos);
              
            dos.append(output);

            dos.flush();
             
        }
        catch (FileNotFoundException fnfe) {
            System.out.println("File not found" + fnfe);
        }
        catch (IOException ioe) {
            System.out.println("Error while writing to file" + ioe);
        }
        finally {
            try {
                if (dos != null) {
                    dos.close();
                }
                if (fos != null) {
                    fos.close();
                }
            }
            catch (Exception e) {
                System.out.println("Error while closing streams" + e);
            }
        }
    }

    public void Read(String fileName){

        String line;
        StringTokenizer st;
        String fileExtension;

	    try {
	        BufferedReader in = new BufferedReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(fileName))));

	        DataInputStream in2 =new DataInputStream(new BufferedInputStream(new FileInputStream(fileName)));

	        // read PPM image header

	        // skip comments
	        line = in.readLine();
	        fileExtension=new String(line);
	        in2.skip((line+"\n").getBytes().length);
	        do {
	            line = in.readLine();
	            in2.skip((line+"\n").getBytes().length);
	        } while (line.charAt(0) == '#');

	        // the current line has dimensions
	        st = new StringTokenizer(line);
	        width = Integer.parseInt(st.nextToken());
	        height = Integer.parseInt(st.nextToken());

	        if (fileExtension.equals("P2") || fileExtension.equals("P3")) {
		        // next line has pixel depth
		        line = in.readLine();
		        in2.skip((line+"\n").getBytes().length);
		        st = new StringTokenizer(line);
		        depth = Integer.parseInt(st.nextToken());
	        }
	        Scanner sc = new Scanner(in2);
	        // read pixels now
	        if (fileExtension.equals("P3")){
		        pixelsP3 = new int[height][width][3];
		        for (int y = 0; y < height; y++){
		            for (int x = 0; x < width; x++){
		                for (int i = 0; i < 3; i++){
		                    pixelsP3[y][x][i] = sc.nextInt();
		                }
		            }
		        }
	    	}

	    	if(fileExtension.equals("P1") || fileExtension.equals("P2")){
	    		pixelsP1P2 = new int[height][width];
		        for (int y = 0; y < height; y++){
		            for (int x = 0; x < width; x++){
		                pixelsP1P2[y][x] = sc.nextInt();
		            }
		        }
                if (fileExtension.equals("P1")) { depth = 1;}
	    	}

	        in.close();
	        in2.close();

	    } catch(ArrayIndexOutOfBoundsException e) {
	        System.out.println("Error: image in "+fileName+" too big");
	    } catch(FileNotFoundException e) {
	        System.out.println("Error: file "+fileName+" not found");
	    } catch(IOException e) {
	        System.out.println("Error: end of stream encountered when reading "+fileName);
	    }
	}
}