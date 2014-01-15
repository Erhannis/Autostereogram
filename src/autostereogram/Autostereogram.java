/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package autostereogram;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;
import mathnstuff.MeMath;

/**
 * Program attempting to make those 3d images made of scribbly dots or whatever.
 * @author mewer
 */
public class Autostereogram {

    public static double[][] genHeightMap(int width, int height) {
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
    
    public static final double EYE_LEFT_OFFSET = -150;
    public static final double EYE_RIGHT_OFFSET = 150;
    public static final double EYES_DISTANCE = 400; // Hmm.  Technically changes based on height.
    
    public static double findBaseCrossing(double ex, double ez, double px, double pz) {
        double d = (ex - px) * (pz / (ez - pz));
        return px - d;
    }
    
    public static BufferedImage genImage(double[][] heightMap) {
        int width = heightMap.length;        
        int height = width > 0 ? heightMap[0].length : 0;
        double cx = width / 2.0;
        double cy = height / 2.0;
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = bi.createGraphics();

        int pts = 100000;
        Random r = new Random();
        for (int i = 0; i < pts; i++) {
            int x = r.nextInt(width);
            int y = r.nextInt(height);
            Color color = new Color(0xFF000000 + r.nextInt(0x1000000), true);
            g.setColor(color);
            double left = findBaseCrossing(EYE_LEFT_OFFSET + cx, EYES_DISTANCE, x, heightMap[x][y]);
            double right = findBaseCrossing(EYE_RIGHT_OFFSET + cx, EYES_DISTANCE, x, heightMap[x][y]);
            int li = (int)left;
            int ri = (int)right;
            if (li >= 0 && li < width) {
                g.drawLine(li, y, li, y);
            }
            if (ri >= 0 && ri < width) {
                g.drawLine(ri, y, ri, y);
            }
            //TODO Add recursion.
        } 
        
        return bi;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        double[][] heightMap = genHeightMap(800, 500);
        BufferedImage bi = genImage(heightMap);
        MainFrame mf = new MainFrame(bi);
        mf.setVisible(true);
    }
}
