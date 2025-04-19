package graphics;

import static org.lwjgl.opengl.GL11.*; 
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import util.bufferUtils;


public class texture {
    
    private int width, height;
    private int texture;

    public texture(String path){

    }

    private int load(String path) {
        int[] pixels = null; //int array w each element being the color of the pixel
        try {
            BufferedImage image = ImageIO.read(new FileInputStream(path));
            width = image.getWidth();
            height = image.getHeight(); 
            pixels = new int[width*height];
            image.getRGB(0,0, width, height, pixels, 0, width);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        //change the order of the color bits passed in to match java
        int[] data = new int[width * height];
        for(int i = 0; i < width*height; i++){
            int a = (pixels[i] & 0xff000000) >> 24;
            int r = (pixels[i] & 0xff0000) >> 16;
            int g = (pixels[i] & 0xff00) >> 8;
            int b = (pixels[i] & 0xff);

            data[i] = a << 24 | b << 16 | g << 8 | r;
        }

        int tex = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, tex); //selected the texture
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST); //aliasing prevention
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, bufferUtils.createIntBuffer(data));
    }
}
