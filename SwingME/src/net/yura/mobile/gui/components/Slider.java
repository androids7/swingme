/*
 *  This file is part of 'yura.net Swing ME'.
 *
 *  'yura.net Swing ME' is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  'yura.net Swing ME' is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with 'yura.net Swing ME'. If not, see <http://www.gnu.org/licenses/>.
 */

package net.yura.mobile.gui.components;

import javax.microedition.lcdui.game.Sprite;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.plaf.Style;
import net.yura.mobile.logging.Logger;

/**
 * @author Yura Mamyrin
 * @see javax.swing.JSlider
 * @see javax.swing.JScrollBar
 */
public class Slider extends Component {

    public static final int MINIMUM_THUMB_SIZE=5;

    private Icon thumbTop;
    private Icon thumbBottom;
    private Icon thumbFill;
    private Icon trackTop;
    private Icon trackBottom;
    private Icon trackFill;


    int min,max,value,extent;
    boolean horizontal = true;
    // Slider has horizontal default
    // Scrollbar has vertical default


    /**
     * Creates a horizontal slider with the range 0 to 100 and an initial value of 50.
     * @see javax.swing.JSlider#JSlider() JSlider.JSlider
     * @see javax.swing.JScrollBar#JScrollBar() JScrollBar.JScrollBar
     */
    public Slider() {
        min = 0;
        max = 100;
        value = 50;

        extent = 1;
    }

    /**
     * @see javax.swing.JSlider#setOrientation(int) JSlider.setOrientation
     * @see javax.swing.JScrollBar#setOrientation(int) JScrollBar.setOrientation
     */
    public void setHorizontal(boolean b) {
        horizontal = b;
    }

    /**
     * @see javax.swing.JSlider#getValue() JSlider.getValue
     * @see javax.swing.JScrollBar#getValue() JScrollBar.getValue
     */
    public Object getValue() {
        return new Integer(value);
    }

    /**
     * @see javax.swing.JSlider#setValue(int) JSlider.setValue
     * @see javax.swing.JScrollBar#setValue(int) JScrollBar.setValue
     */
    public void setValue(Object o) {
        if (o instanceof Integer) {
            value = ((Integer)o).intValue();
        }
        //#mdebug warn
        else {
            Logger.warn("trying to set value that is not a Integer "+o);
        }
        //#enddebug
    }

    /**
     * @see javax.swing.JSlider#getMaximum() JSlider.getMaximum
     * @see javax.swing.JScrollBar#getMaximum() JScrollBar.getMaximum
     */
    public int getMaximum() {
        return max;
    }

    /**
     * @see javax.swing.JSlider#getMinimum() JSlider.getMinimum
     * @see javax.swing.JScrollBar#getMinimum() JScrollBar.getMinimum
     */
    public int getMinimum() {
        return min;
    }

    /**
     * @see javax.swing.JSlider#getExtent() JSlider.getExtent
     * @see javax.swing.JScrollBar#getVisibleAmount() JScrollBar.getVisibleAmount
     */
    public int getExtent() {
        return extent;
    }



    /**
     * @see javax.swing.JSlider#setMaximum(int) JSlider.setMaximum
     * @see javax.swing.JScrollBar#setMaximum(int) JScrollBar.setMaximum
     */
    public void setMaximum(int m) {
        max = m;
    }

    /**
     * @see javax.swing.JSlider#setMinimum(int) JSlider.setMinimum
     * @see javax.swing.JScrollBar#setMinimum(int) JScrollBar.setMinimum
     */
    public void setMinimum(int m) {
        min = m;
    }

    /**
     * @see javax.swing.JSlider#setExtent(int) JSlider.setExtent
     * @see javax.swing.JScrollBar#setVisibleAmount(int) JScrollBar.setVisibleAmount
     */
    public void setExtent(int ex) {
        extent = ex;
    }





    public void paintComponent(Graphics2D g) {
        if (horizontal) {

            int t = g.getTransform();
            g.setTransform( Sprite.TRANS_MIRROR_ROT270 );

            drawScrollBar(g,
                    0,
                    0,
                    height,
                    width,
                    value,
                    extent,
                    max
            );

            g.setTransform( t );

        }
        else {

            drawScrollBar(g,
                    0,
                    0,
                    width,
                    height,
                    value,
                    extent,
                    max
            );

        }
    }


    public void processMouseEvent(int type, int pointX, int pointY, KeyEvent keys) {

        int click;

        if (horizontal) {

            click = doClickInScrollbar(
                    0,
                    0,
                    height,
                    width,
                    value,
                    extent,
                    max,
                    pointY,
                    pointX
            );
        }
        else {

            click = doClickInScrollbar(
                    0,
                    0,
                    width,
                    height,
                    value,
                    extent,
                    max,
                    pointX,
                    pointY
            );

        }

        System.out.println("TODO click="+click);

    }

    public void workoutMinimumSize() {

        int thickness = (trackTop != null) ? trackTop.getIconWidth() : 0;

        if (horizontal) {
            width = 20;
            height = thickness;
        }
        else {
            width = thickness;
            height = 20;
        }
    }

    protected String getDefaultName() {
        return "Slider";
    }

    public void updateUI() {
        super.updateUI();

        thumbTop = (Icon)theme.getProperty("thumbTop", Style.ALL);
        thumbBottom = (Icon) theme.getProperty("thumbBottom", Style.ALL);
        thumbFill = (Icon)theme.getProperty("thumbFill", Style.ALL);
        trackTop = (Icon) theme.getProperty("trackTop", Style.ALL);
        trackBottom = (Icon) theme.getProperty("trackBottom", Style.ALL);
        trackFill = (Icon)theme.getProperty("trackFill", Style.ALL);
    }





    public static final int CLICK_NONE = 0;
    public static final int CLICK_UP = 1;
    public static final int CLICK_PGUP = 2;
    public static final int CLICK_THUMB = 3;
    public static final int CLICK_PGDOWN = 4;
    public static final int CLICK_DOWN = 5;

    public int doClickInScrollbar(int x1,int y1,int w1,int h1,int value1,int extent1, int max1, int pointX,int pointY) {

        int[] tmp = getOffsets(x1,y1,w1,h1,value1,extent1,max1);
        int box = tmp[0];
        int starty = tmp[1];
        int extenth = tmp[2];

        if (ScrollPane.isPointInsideRect(pointX, pointY, x1, y1, w1, box)) {
            return CLICK_UP;
        }
        else if (ScrollPane.isPointInsideRect(pointX, pointY, x1, y1+box, w1, starty-y1-box)) {
            return CLICK_PGUP;
        }
        else if (ScrollPane.isPointInsideRect(pointX, pointY, x1, starty ,w1,extenth)) { // thumb on the right
            return CLICK_THUMB;
        }
        else if (ScrollPane.isPointInsideRect(pointX, pointY, x1, starty+extenth, w1, h1 - box - (starty-y1) - extenth)) {
            return CLICK_PGDOWN;
        }
        else if (ScrollPane.isPointInsideRect(pointX, pointY, x1, y1+h1-box, w1, box)) {
            return CLICK_DOWN;
        }
        else {
            return CLICK_NONE;
        }

    }

    /**
     * @see javax.swing.JScrollBar#JScrollBar(int, int, int, int, int) JScrollBar.JScrollBar
     */
    public void drawScrollBar(Graphics2D g, int x,int y,int w,int h,int value,int extent, int max
//            ,Icon trackTop,Icon trackFill,Icon trackBottom,Icon thumbTop,Icon thumbFill,Icon thumbBottom
            ) {

        int starty = 0;
        int extenth = h;

        // DRAW ARROWS
        //,��,����``����,��,����``����,��,����``����,��,����``����,��,����``����,��,

        if (trackTop!=null) {

            int x1 = x + (w-trackTop.getIconWidth())/2;
            trackTop.paintIcon(this, g, x1, y);
            trackBottom.paintIcon(this, g, x1, y+h-trackBottom.getIconHeight() );

            starty = trackTop.getIconHeight();
            extenth = h - starty - trackBottom.getIconHeight();

        }

        // draw the track fill color
        //,��,����``����,��,����``����,��,����``����,��,����``����,��,����``����,��,

        if (trackFill!=null) {
            tileIcon(g, trackFill, x + (w-trackFill.getIconWidth())/2 , starty, trackFill.getIconWidth(), extenth);
        }

        // draw the thumb!
        //,��,����``����,��,����``����,��,����``����,��,����``����,��,����``����,��,

        int[] tmp = getOffsets(x,y,w,h,value,extent,max
//                ,trackTop,trackFill,trackBottom,thumbTop,thumbFill,thumbBottom
                );
        starty = tmp[1];
        extenth = tmp[2];

        if (thumbTop!=null) {
            int x1 = x + (w-thumbTop.getIconWidth())/2;
            thumbTop.paintIcon(this, g, x1, starty);
            thumbBottom.paintIcon(this, g, x1, starty+extenth-thumbBottom.getIconHeight());
            starty = starty + thumbTop.getIconHeight();
            extenth = extenth - (thumbTop.getIconHeight()+thumbBottom.getIconHeight());
        }

        if (thumbFill!=null) {
            tileIcon(g, thumbFill, x + (w-thumbFill.getIconWidth())/2 , starty, thumbFill.getIconWidth(), extenth);
        }

    }

    private void tileIcon(Graphics2D g, Icon icon,int dest_x,int dest_y,int dest_w,int dest_h) {
        int h = icon.getIconHeight();

        final int[] c = g.getClip();

        g.clipRect(dest_x,dest_y,dest_w,dest_h);

        for (int pos_y=dest_y;pos_y<(dest_y+dest_h);pos_y=pos_y+h) {
            icon.paintIcon(this, g, dest_x, pos_y);
        }

        icon.paintIcon(this, g, 0, 0);

        g.setClip(c);

    }

    /**
     * @param x - Ignored?
     * @param y - Start of bar
     * @param w - width of bar
     * @param h - viewPort Height/Width
     * @param value - Desired view X/Y
     * @param extent - viewPort Height/Width > same as h?
     * @param max - view Height/Width
     * @return
     */
    public int[] getOffsets(int x,int y,int w, int h, int value,int extent, int max
//            ,Icon trackTop,Icon trackFill,Icon trackBottom,Icon thumbTop,Icon thumbFill,Icon thumbBottom
            ) {

        final int box;
        final int topBotton;
        if (trackTop!=null) {
            box = (trackTop.getIconHeight() >w)?w:trackTop.getIconHeight();
            topBotton = (thumbTop==null)?0:thumbTop.getIconHeight()+thumbBottom.getIconHeight();
        }
        else {
            box = 0;
            topBotton = 0;
        }
        final int space1 = h - box * 2;

        int extenth = (int) ( (extent*space1)/(double)max + 0.5);
        int min = (topBotton<MINIMUM_THUMB_SIZE)?MINIMUM_THUMB_SIZE:topBotton;
        min = min>(space1/2)?space1/2:min;

        int space = space1;
        if (extenth < min) {
            extenth = min;
            space = space-extenth;
            max = max-extent;
        }
        int starty = box+ (int)( (space*value)/(double)max + 0.5 );

        // make sure the thumb value is bound
        /*
         // this works but is not that nice
        if (starty < box) {
            starty = box;
        }
        else if ((starty+extenth) > (box+space1)) {
            starty = box + space1 - extenth;
        }
         */
        // add squidge!
        if ((starty+extenth) < (box+min)) {
            starty = box;
            extenth = min;
        }
        else if (starty < box) {
            extenth = starty+extenth-box;
            starty = box;
        }
        else if (starty > (box+space1-min)) {
            starty = box+space1-min;
            extenth = min;
        }
        else if ((starty+extenth) > (box+space1)) {
            extenth = box+space1-starty;
        }

        return new int[] {box,y+starty,extenth};
    }

    public int getNewValue(int x,int y,int w,int h,int value,int extent, int max,int pixels) {
        int[] offsets = getOffsets(x, y, w, h, 0, extent, max);
        return value + ((max-extent)*  pixels)/ (h - offsets[0]*2 - offsets[2]);
    }

}
