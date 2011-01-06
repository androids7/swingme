package net.yura.mobile.gui.plaf.nimbus;

import java.util.Vector;
import javax.microedition.lcdui.game.Sprite;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.plaf.LookAndFeel;

/**
 * @author Nathan
 */
public class NimbusIcon extends Icon {

    int type;
    int color;
    Vector borders;

    public NimbusIcon(int size, int type, Vector borders, int color) {
//        if ((size%2)==0) {
//            size++;
//        }
        width = size;
        height = size;
        this.type = type;
        this.borders = borders;
        this.color = color;

    }

    public void paintIcon(Component c, Graphics2D g, int x, int y) {

        //Logger.debug(c.getName()+" icon at "+x+" "+y);

        // Draw aether borders
//        if ((type == LookAndFeel.ICON_RADIO)) {
//            int corner = 4;//width/2;
//            int[] corners = {corner,corner,corner,corner};
//            for (int b = 0 ; b<borders.size() ; b++) {
//                NimbusBorderSetting border = (NimbusBorderSetting) borders.elementAt(b);
//                border.corner = corners;
//            }
//        }

        
        NimbusBorder.paintBorders(borders, g, x, y, getIconWidth(), getIconHeight());

        g.setColor(color);

        // Draw the checkbox tick
        if (type == LookAndFeel.ICON_CHECKBOX) {

            if (c instanceof Button) {
                Button b = (Button)c;
                if (b.isSelected()) {
                    int w = getIconWidth();
                    int h = getIconHeight();                    
                    for (int pad=3;pad<6;pad++) {
                        g.drawLine(x+pad, y+h/2, x+w/3, y+h-pad);
                        g.drawLine(x+w/3, y+h-pad,x+w-pad,y+pad);
                    }
                }
            }

        }
        // Draw a radio icon
        else if (type == LookAndFeel.ICON_RADIO) {
            int w = getIconWidth();
            int h = getIconHeight();

            if (c instanceof Button) {
                Button b = (Button)c;
                if (b.isSelected()){
                    int w2 = borders.size()+1;
                    int h2 = borders.size()+1;
                    g.fillArc(x+w2, y+h2, w-(w2*2), h-(h2*2), 0, 360);
                }
            }
        }
        else if (type == LookAndFeel.ICON_ARROW_UP) {
            int top = (height/2)-3;
            int middle = (width/2);

            g.fillTriangle(x+middle, y+top,
                           x+middle-3, y+top+6,
                           x+middle+3, y+top+6);
        }
        else if (type == LookAndFeel.ICON_ARROW_DOWN || type == LookAndFeel.ICON_COMBO) {
            int top = (height/2)-3;
            int middle = (width/2);

            g.fillTriangle(x+middle, y+top+6,
                           x+middle-3, y+top,
                           x+middle+3, y+top);
        }
        else if ((type == LookAndFeel.ICON_SPINNER_LEFT) || (type == LookAndFeel.ICON_ARROW_LEFT)) {
            int top = (height/2)-3;
            int middle = (width/2);

            g.fillTriangle(x+middle+3, y+top,
                           x+middle-3, y+top+3,
                           x+middle+3, y+top+6);
        }
        else if ((type == LookAndFeel.ICON_SPINNER_RIGHT) || (type == LookAndFeel.ICON_ARROW_RIGHT)) {
            int top = (height/2)-3;
            int middle = (width/2);

            g.fillTriangle(x+middle-3, y+top+6,
                           x+middle-3, y+top,
                           x+middle+3, y+top+3);
        }
    }
    
}
