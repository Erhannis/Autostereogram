/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package autostereogram;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;
import mathnstuff.MeMath;

/**
 * Program attempting to make those 3d images made of scribbly dots or whatever.
 * @author mewer
 */
public class Autostereogram {

    public double[][] genHeightMap(int width, int height) {
        double[][] result = new double[width][height];
        double radius = Math.min(width, height) * 0.4;
        double r2 = radius * radius;
        double cx = width / 2.0;
        double cy = height / 2.0;
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (MeMath.sqr(cx - x) + MeMath.sqr(cy - y) <= r2) {
                    result[x][y] = 50;
                } else {
                    result[x][y] = 0;
                }
            }
        }
        
        return result;
    }
    
    public BufferedImage genImage(double[][] heightMap) {
        int width = heightMap.length;        
        int height = width > 0 ? heightMap[0].length : 0;
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = bi.createGraphics();

        int pts = 1000;
        Random r = new Random();
        for (int i = 0; i < pts; i++) {
            
        } 
        
        return bi;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    }
}
