package net.yura.mobile.gui.components;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.layout.BorderLayout;
import net.yura.mobile.gui.layout.GridLayout;

/**
 * @author Yura Mamyrin
 */
public class TitleBar extends Panel implements ActionListener {

    public TitleBar(String title,Image icon,boolean resize,boolean move,boolean hide,boolean max,boolean close) {
     
        super(new BorderLayout());
        
        
        Panel buttonPanel = new Panel( new GridLayout(1,0,2) );
        if (resize) {
            Button b = new Button("#");
            buttonPanel.add(b);
            b.addActionListener(this);
            b.setActionCommand("resize");
        }
        if (move) {
            Button b = new Button("+");
            buttonPanel.add(b);
            b.addActionListener(this);
            b.setActionCommand("move");
        }
        if (hide) {
            Button b = new Button("_");
            b.addActionListener(this);
            b.setActionCommand("hide");
            buttonPanel.add(b);
        }
        if (max) {
            Button b = new Button("[]");
            b.addActionListener(this);
            b.setActionCommand("max");
            buttonPanel.add(b);
        }
        if (close) {
            Button b = new Button("X");
            b.addActionListener(this);
            b.setActionCommand("close");
            buttonPanel.add(b);

        }

        add(new Label( title,icon ));
        add(buttonPanel,Graphics.RIGHT);
        setBackground(0x00AAAAFF);

    }
    
    private int oldX,oldY;
    private boolean move,resize;
    private Component old;
    
    public void pointerEvent(int type, int x, int y) {

        if (type == DesktopPane.PRESSED) {
            oldX=x;
            oldY=y;

        }
        else if (type == DesktopPane.DRAGGED) {

            owner.setLocation(owner.getX()+(x-oldX),owner.getY()+(y-oldY));
            DesktopPane.getDesktopPane().fullRepaint();

        }

    }

    public boolean keyEvent(KeyEvent keypad) {

            if (keypad.isDownAction(Canvas.LEFT)) {
                
                if (move) { owner.setLocation(owner.getX()-2,owner.getY()); }
                else if (resize) { owner.setSize(owner.getWidth()-2, owner.getHeight()); }
                DesktopPane.getDesktopPane().fullRepaint();
                return true;
            }
            if (keypad.isDownAction(Canvas.RIGHT)) {
                
                if (move) { owner.setLocation(owner.getX()+2,owner.getY()); }
                else if (resize) { owner.setSize(owner.getWidth()+2, owner.getHeight()); }
                DesktopPane.getDesktopPane().fullRepaint();
                return true;
            }
            if (keypad.isDownAction(Canvas.UP)) {
                
                if (move) { owner.setLocation(owner.getX(),owner.getY()-2); }
                else if (resize) { owner.setSize(owner.getWidth(), owner.getHeight()-2); }
                DesktopPane.getDesktopPane().fullRepaint();
                return true;
            }
            if (keypad.isDownAction(Canvas.DOWN)) {
                
                if (move) { owner.setLocation(owner.getX(),owner.getY()+2); }
                else if (resize) { owner.setSize(owner.getWidth(), owner.getHeight()+2); }
                DesktopPane.getDesktopPane().fullRepaint();
                return true;
            }
            if (keypad.isDownAction(Canvas.FIRE)) {
                
                move = false;
                resize = false;
                DesktopPane.getDesktopPane().setFocusedComponent(old);
                return true;
            }
            return false;
    }
    
    // if we loseFocus to some random component then resize or move can STILL be true
    public void actionPerformed(String actionCommand) {

        if (resize || move) {
                move = false;
                resize = false;
        }
        else if ("move".equals(actionCommand)) {
 
                old = DesktopPane.getDesktopPane().getFocusedComponent();
                DesktopPane.getDesktopPane().setFocusedComponent(this);
                move = true;

        }
        else if ("resize".equals(actionCommand)) {
 
                old = DesktopPane.getDesktopPane().getFocusedComponent();
                DesktopPane.getDesktopPane().setFocusedComponent(this);
                resize = true;

        }
        else {
            owner.actionPerformed(actionCommand);
        }
    }

    
}
