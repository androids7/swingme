package net.yura.android.plaf;

import java.util.Vector;

import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.border.Border;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.plaf.Style;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class AndroidBorder implements Border {

    private Drawable drawable;
    private Rect padding;

    public AndroidBorder(Drawable d) {
        this.drawable = d;
        this.padding = new Rect();
        d.getPadding(padding);
    }

    public AndroidBorder(Drawable d, Rect extraPadding) {
        this(d);
        padding.set(
            padding.left + extraPadding.left,
            padding.top + extraPadding.top,
            padding.right + extraPadding.right,
            padding.bottom + extraPadding.bottom
        );
    }

    public int getBottom() {
        return padding.bottom;
    }

    public int getLeft() {
        return padding.left;
    }

    public int getRight() {
        return padding.right;
    }

    public int getTop() {
        return padding.top;
    }

    public boolean isBorderOpaque() {
        return (drawable.getOpacity() == PixelFormat.OPAQUE);
    }

    public void paintBorder(Component c, Graphics2D g, int width, int height) {

        int state = c.getCurrentState();
        setDrawableState(state, drawable);

        android.graphics.Canvas canvas = g.getGraphics().getCanvas();
        canvas.save();
        canvas.clipRect(-getLeft(), -getTop(), width+getRight(), height+getBottom());
        drawable.setBounds(-getLeft(), -getTop(), width+getRight(), height+getBottom());
        drawable.draw(canvas);
        canvas.restore();
    }

    static int[] getDrawableState(int state) {
        Vector stateList = new Vector(3);

        stateList.add(new Integer(android.R.attr.state_window_focused));

        if ((state & Style.DISABLED) == 0) {
            stateList.add(new Integer(android.R.attr.state_enabled));
        }

        if ((state & Style.FOCUSED) != 0) {
            stateList.add(new Integer(android.R.attr.state_focused));
        }

        if ((state & Style.SELECTED) != 0) {
            stateList.add(new Integer(android.R.attr.state_selected));
            stateList.add(new Integer(android.R.attr.state_checked));
        }

        int[] res = new int[stateList.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = ((Integer) stateList.elementAt(i)).intValue();
        }

        return res;
    }

    static void setDrawableState(int state, Drawable drawable) {
        drawable.setState(getDrawableState(state));
    }

}