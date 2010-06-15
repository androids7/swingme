package net.yura.mobile.gui.components;

import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.Icon;

public class ImageView extends Component {

    private Icon bgImage;
    private int imgW, imgH;
    boolean consumingMotionEvents;

    // TODO: Animate the resize to the size of SP
    // TODO: Center the zoom on the bit that is being zoomed.
    // BUGS: While SP is animating > pinch > release


    public void setBackgroundImage(Icon backgroundImage) {
        this.bgImage = backgroundImage;

        if (bgImage != null) {
            imgW = bgImage.getIconWidth();
            imgH = bgImage.getIconHeight();
        }

        //DELETE:
        setBackground(0xFF0000FF);
    }

    public Icon getBackgroundImage() {
        return bgImage;
    }

    // Override
    public void workoutMinimumSize() {
        // TODO Auto-generated method stub
        width = 100;
        height = 100;
    }

    // Override
    public boolean consumesMotionEvents() {
        return consumingMotionEvents;
    }

    // Override
    public void paintComponent(Graphics2D g) {

        double ratio = Math.min(getHeight()/(double)imgH,getWidth()/(double)imgW);
        int imgX = (int) (getWidth() - (imgW * ratio)) / 2;
        int imgY = (int) (getHeight() - (imgH * ratio)) / 2;

        g.translate(imgX, imgY);
        g.getGraphics().scale(ratio, ratio);

        bgImage.paintIcon(this, g, 0, 0);

        g.setColor(0xFF00FF00);
        int mx = startPinchX;
        int my = startPinchY;
        g.drawLine(mx - 5, my, mx + 5, my);
        g.drawLine(mx, my - 5, mx, my + 5);


        g.getGraphics().scale(1 / ratio, 1 / ratio);




        g.setColor(0xFF00FF00);
        g.drawRect(0, 0, width, height);


        g.translate(-imgX, -imgY);

        if (px != null) {
            g.setColor(0xFFFF0000);
            g.drawRect(Math.min(px[0], px[1]),
                       Math.min(py[0], py[1]),
                       Math.max(1, Math.abs(px[1] - px[0])),
                       Math.max(1, Math.abs(py[1] - py[0])));
        }
    }

    int startPinchSize;
    int startPinchX, startPinchY;
    int newPinchX, newPinchY; // TODO: can be local

    int[] px;
    int[] py;

    double ratio = 1.0;
    int imgX, imgY;

    // Override
    public void processMultitouchEvent(int[] type, int[] x, int[] y) {

        consumingMotionEvents = (type[0] != DesktopPane.RELEASED);

        System.out.println("ImageView: pointerEvent " + " consumingMotionEvents = " + consumingMotionEvents);

        if (type.length >= 2) {
            px = x;
            py = y;

            int pinchDiff = 0;

            if (type[0] == DesktopPane.PRESSED || type[1] == DesktopPane.PRESSED) {

                // Stop any animation
                animateToFit(false);

                ratio = Math.min(getHeight()/(double)imgH,getWidth()/(double)imgW);

                startPinchSize = getDistance(x, y);

                int imgX = (int) (getWidth() - (imgW * ratio)) / 2;
                int imgY = (int) (getHeight() - (imgH * ratio)) / 2;

                startPinchX = (int) ((((x[0] + x[1]) / 2) - imgX)/ratio);
                startPinchY = (int) ((((y[0] + y[1]) / 2) - imgY)/ratio);

                posX = posX + imgX;
                posY = posY + imgY;

                System.out.println("PRESSED " + startPinchSize);
            }
            else {
                int pinchSize = getDistance(x, y);
                pinchDiff = (pinchSize - startPinchSize);

                System.out.println("DRAGGED/RELEASED " + pinchSize);
            }

            int newW = (int)(imgW*ratio) + pinchDiff;
            int newH = (imgH * newW) / imgW;

            // TODO: Should check the new w/h bounds... e.g. not zooming more
            // than 2x screen size, or img size... and less than 90% of the
            // sreen?

            width = newW;
            height = newH;

            if (type[0] != DesktopPane.PRESSED && type[1] != DesktopPane.PRESSED) {

                // here we assume that the panel is already the same size as the image!!

                double nratio = width/(double)imgW;

                int endPinchX = (x[0] + x[1]) / 2;
                int endPinchY = (y[0] + y[1]) / 2;

                int difx = (int)(startPinchX*nratio - endPinchX);
                int dify = (int)(startPinchY*nratio - endPinchY);

                posX = posX - difx;
                posY = posY - dify;
            }

            if (type[0] == DesktopPane.RELEASED || type[1] == DesktopPane.RELEASED) {
                // Start animation
                animateToFit(true);
            }
        }

        // TODO: Remove?
        Component cmp = (getParent() == null) ? this : getParent();
        cmp.repaint();
    }

    private int getDistance(int[] x, int[] y) {
        int dx = x[0] - x[1];
        int dy = y[0] - y[1];
        return (int) Math.sqrt(dx * dx + dy * dy);
    }

    // Override
    protected String getDefaultName() {
        return "ImageView";
    }

    private void animateToFit(boolean startAnimation) {
        Component sp = getParent();
        if (sp instanceof ScrollPane) {
            ((ScrollPane)sp).animateToFit(startAnimation);
        }
    }
}
