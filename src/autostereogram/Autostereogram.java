/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package autostereogram;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import mathnstuff.MeMath;

/**
 * Program attempting to make those 3d images made of scribbly dots or whatever.
 * Note that I kinda ignore slanting in the y direction, as if your eyes were
 * two vertical cylinders, parallel to the image.
 * @author mewer
 */
public class Autostereogram {
    
    public static final double EYE_LEFT_OFFSET = -150.5;
    public static final double EYE_RIGHT_OFFSET = 150.5;
    public static final double EYES_DISTANCE = 1400; // Hmm.  Technically changes based on height.

    public static void drawFunction(Graphics2D g, int x, int y) {
        int offset = 2;
        g.drawOval(x - offset, y - offset, 1 + (2 * offset), 1 + (2 * offset));
    }
    
    public static double[][] genHeightMap(int width, int height) {
        double[][] result = new double[width][height];
        double radius = Math.min(width, height) * 0.4;
        double r2 = radius * radius;
        double cx = width / 2.0;
        double cy = height / 2.0;
        double base = 100;
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (MeMath.sqr(cx - x) + MeMath.sqr(cy - y) <= r2) {
                    result[x][y] = base + Math.sqrt(r2 - (MeMath.sqr(cx - x) + MeMath.sqr(cy - y)));
                } else {
                    result[x][y] = base;
                }
            }
        }
        
        return result;
    }
    
    public static double findBaseCrossing(double ex, double ez, double px, double pz) {
        double d = (ex - px) * (pz / (ez - pz));
        return px - d;
    }
    
    /**
     * Some suspicious rounding may happen here.
     */
    public static ArrayList<Integer> leftSurfaceCrossings(double[][] heightMap, int y, double px, double elx, double elz) {
        //TODO Account for perpendicular line.
        ArrayList<Integer> results = new ArrayList<Integer>();
        double pz = 0;
        
        //TODO Ugh, the initial value actually depends on the slope of the line.
        boolean geq = true; // Was surface height greater than or equal to projected-onto-line height?
        int tx = heightMap.length - 1;
        double tz = elz + ((pz - elz) * ((tx - elx) / (px - elx)));
        if (heightMap[tx][y] < tz) {
            geq = false;
        } else {
            geq = true;
        }
        for (tx = heightMap.length - 1; tx >= 0; tx--) {
            tz = elz + ((pz - elz) * ((tx - elx) / (px - elx)));
            if (geq) {
                if (heightMap[tx][y] < tz) {
                    geq = false;
                    results.add(tx);
                } else if (heightMap[tx][y] == tz) {
                    //THINK Hmm.  Should we add it?
                }
            } else {
                if (heightMap[tx][y] >= tz) {
                    geq = true;
                    results.add(tx);
                }
            }
        }
        
        return results;
    }

    /**
     * Some suspicious rounding may happen here.
     */
    public static ArrayList<Integer> rightSurfaceCrossings(double[][] heightMap, int y, double px, double erx, double erz) {
        //TODO Account for perpendicular line.
        ArrayList<Integer> results = new ArrayList<Integer>();
        double pz = 0;
        
        //TODO Ugh, the initial value actually depends on the slope of the line.
        boolean geq = true; // Was surface height greater than or equal to projected-onto-line height?
        int tx = 0;
        double tz = erz + ((pz - erz) * ((tx - erx) / (px - erx)));
        if (heightMap[tx][y] < tz) {
            geq = false;
        } else {
            geq = true;
        }
        for (tx = 0; tx < heightMap.length; tx++) {
            tz = erz + ((pz - erz) * ((tx - erx) / (px - erx)));
            if (geq) {
                if (heightMap[tx][y] < tz) {
                    geq = false;
                    results.add(tx);
                } else if (heightMap[tx][y] == tz) {
                    //THINK Hmm.  Should we add it?
                }
            } else {
                if (heightMap[tx][y] >= tz) {
                    geq = true;
                    results.add(tx);
                }
            }
        }
        
        return results;
    }

    public static void leftRecurse(Graphics2D g, double[][] heightMap, int y, double px, double elx, double elz, double erx, double erz) {
        ArrayList<Integer> crossings = leftSurfaceCrossings(heightMap, y, px, elx, elz);
        
        for (int i : crossings) {
            double left = findBaseCrossing(erx, erz, i, heightMap[i][y]);
            int li = (int)left;
            if (li >= 0 && li < heightMap.length && left < px - 1) { // (want to make sure it will terminate)
                //TODO Consider how to incorporate points to the right of px.
                drawFunction(g, li, y);
                leftRecurse(g, heightMap, y, left, elx, elz, erx, erz);
            }
        }
    }

    public static void rightRecurse(Graphics2D g, double[][] heightMap, int y, double px, double elx, double elz, double erx, double erz) {
        ArrayList<Integer> crossings = rightSurfaceCrossings(heightMap, y, px, erx, erz);
        
        for (int i : crossings) {
            double right = findBaseCrossing(elx, elz, i, heightMap[i][y]);
            int ri = (int)right;
            if (ri >= 0 && ri < heightMap.length && right > px + 1) { // (want to make sure it will terminate)
                //TODO Consider how to incorporate points to the right of px.
                drawFunction(g, ri, y);
                rightRecurse(g, heightMap, y, right, elx, elz, erx, erz);
            }
        }
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
            g.setPaint(color);
            double right = findBaseCrossing(EYE_LEFT_OFFSET + cx, EYES_DISTANCE, x, heightMap[x][y]);
            double left = findBaseCrossing(EYE_RIGHT_OFFSET + cx, EYES_DISTANCE, x, heightMap[x][y]);
            int li = (int)left;
            int ri = (int)right;
            if (li >= 0 && li < width) {
                drawFunction(g, li, y);
                leftRecurse(g, heightMap, y, left, EYE_LEFT_OFFSET + cx, EYES_DISTANCE, EYE_RIGHT_OFFSET + cx, EYES_DISTANCE);
            }
            if (ri >= 0 && ri < width) {
                drawFunction(g, ri, y);
                rightRecurse(g, heightMap, y, right, EYE_LEFT_OFFSET + cx, EYES_DISTANCE, EYE_RIGHT_OFFSET + cx, EYES_DISTANCE);
            }
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
