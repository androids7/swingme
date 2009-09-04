package net.yura.mobile.gui.border;

import net.yura.mobile.gui.Font;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.components.Component;

/**
 * @author Administrator
 */
public class TitledBorder implements Border {

    private static final int LEFT_OFFSET=10;

    protected String title;
    protected Border border;
    protected Font   titleFont;
    protected int  titleColor;

    public TitledBorder(Border border, String title,Font f) {
        this.border = border;
        if (border==null) { border = Component.empty; }
        titleFont = f;
        this.title = title;
    }

    public void paintBorder(Component c, Graphics2D g, int width, int height) {
        int topOffset = (getTop() - border.getTop()) /2;
        int leftOffset = getLeft()+LEFT_OFFSET;
        g.translate(0, -topOffset);

        int[] clip = g.getClip();
        // paint strip left of text
        int fullHeight = height+topOffset+border.getTop()+border.getBottom();
        g.clipRect(-getLeft(), -border.getTop(), leftOffset, fullHeight);
        border.paintBorder(c, g, width, height+topOffset);
        g.setClip(clip);

        // paint strip right of text
        int textWidth = titleFont.getWidth(title)+2;
        g.clipRect(textWidth+LEFT_OFFSET, -border.getTop(), width-LEFT_OFFSET-textWidth+border.getRight(), fullHeight);
        border.paintBorder(c, g, width, height+topOffset);
        g.setClip(clip);

        // paint strip below text
        g.clipRect(LEFT_OFFSET, topOffset, textWidth, height+border.getBottom());
        border.paintBorder(c, g, width, height+topOffset);
        g.setClip(clip);
        g.translate(0, topOffset);

        g.setFont(titleFont);
        g.setColor(titleColor);
        g.drawString(title, leftOffset, -getTop() + (getTop() - titleFont.getHeight())/2 );

    }

    public int getTop() {
        return Math.max(border.getTop(), titleFont.getHeight());
    }

    public int getBottom() {
        return border.getBottom();
    }

    public int getRight() {
        return border.getRight();
    }

    public int getLeft() {
        return border.getLeft();
    }

    public boolean isBorderOpaque() {
        return border.isBorderOpaque();
    }

}
