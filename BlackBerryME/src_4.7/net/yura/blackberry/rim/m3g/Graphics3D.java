package net.yura.blackberry.rim.m3g;

import java.util.Hashtable;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.Bitmap;
import net.yura.blackberry.rim.Graphics;

public final class Graphics3D {

    public static final int TRUE_COLOR = 8;

    private static Graphics3D instance;

    private Graphics targetGraphics;
    private int vpX = 0;
    private int vpY = 0;
    private int vpW = 0;
    private int vpH = 0;

    private Graphics3D() {
    }

    public static Graphics3D getInstance() {

        if (instance == null) {
            instance = new Graphics3D();
        }
        return instance;
    }

    public void bindTarget(Object target, boolean depthBuffer, int hints) {

        targetGraphics = (Graphics) target;
    }

    public void releaseTarget() {

    }

    public void clear(Background background) {
        if (background == null) {
            // clear to black
            targetGraphics.setColor(0xff000000);
            targetGraphics.fillRect(vpX, vpY, vpW, vpH);
        }
        else if (background.getImage() == null) {
            // clear to target color
            targetGraphics.setColor(background.getColor());
            targetGraphics.fillRect(vpX, vpY, vpW, vpH);
        }
        else {

            // here is code showing how to scale a image into a destination x,y,w,h
            
            Bitmap img = background.getImage().getImage().getBitmap();
            
            int width = img.getWidth();
            int height = img.getHeight();
            
            if (targetGraphics.bitmap!=null) {
            
                // THIS IS API-5.0
                img.scaleInto(0, 0, width, height, targetGraphics.bitmap, vpX, vpY, vpW, vpH, Bitmap.FILTER_BILINEAR );
            }
            else {

                // destination box
                int[] x = new int[] {0, vpW, vpW, 0};
                int[] y = new int[] {0, 0, vpH, vpH};
                
                int zero = Fixed32.toFP(0);
                
                // make a scale matrix
                int dux = Fixed32.div(Fixed32.toFP(width), Fixed32.toFP(vpW));
                int dvx = zero;
                int duy = zero;
                int dvy = Fixed32.div(Fixed32.toFP(height), Fixed32.toFP(vpH));
                
                targetGraphics.translate(vpX, vpY);
    
                net.rim.device.api.ui.Graphics g = targetGraphics.getGraphics();
                
                g.translate(targetGraphics.getTranslateX(),targetGraphics.getTranslateY());
                g.drawTexturedPath(x, y, null, null, 0, 0, dux, dvx, duy, dvy, img);
                g.translate(-targetGraphics.getTranslateX(),-targetGraphics.getTranslateY());
                
                targetGraphics.translate(-vpX, -vpY);
            }
        }
    }

    public void setViewport(int x, int y, int width, int height) {
        this.vpX = x;
        this.vpY = y;
        this.vpW = width;
        this.vpH = height;
    }

    public static Hashtable getProperties() {
        return new Hashtable(0);
    }
}
