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

package net.yura.mobile.gui.plaf;

import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import net.yura.mobile.gui.Font;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.border.EmptyBorder;
import net.yura.mobile.gui.border.MatteBorder;
import net.yura.mobile.util.StringUtil;
import org.kxml2.io.KXmlParser;

/**
 * @author Yura Mamyrin
 * @see javax.swing.plaf.synth.SynthLookAndFeel
 */
public class SynthLookAndFeel extends LookAndFeel {

    private Style defaultStyle;
    private Hashtable fonts;

    protected Image getImage( String path ) {
        try {
            return Image.createImage(path);
        }
        catch (Exception ex) {
            //#mdebug
            System.err.println("can not load image: "+path);
            ex.printStackTrace();
            //#enddebug
            return null;
        }
    }
    protected InputStream getResourceAsStream(String path) {
        try {
            return getClass().getResourceAsStream(path);
        }
        catch (Exception ex) {
            //#mdebug
            System.err.println("can not load resource: "+path);
            ex.printStackTrace();
            //#enddebug
            return null;
        }
    }
    protected int parseInt(String value,int base) {

        if (value.startsWith("#")) {
            base=16;
            value=value.substring(1);
        }
        else if (value.startsWith("0x")) {
            base=16;
            value=value.substring(2);
        }

        return Integer.parseInt(value, base);
    }

    /**
     * @param input
     * @throws java.lang.Exception
     * @see javax.swing.plaf.synth.SynthLookAndFeel#load(java.io.InputStream, java.lang.Class) SynthLookAndFeel.load
     */
        public void load(InputStream input) throws Exception {

            fonts = new Hashtable();

            KXmlParser parser = new KXmlParser();
            parser.setInput(input, null);
            parser.nextTag();

            Hashtable styleList = new Hashtable();

            // read start tag
            while (parser.nextTag() != KXmlParser.END_TAG) {

                String name = parser.getName();
                
                if ("style".equals(name)){
                    
                    String id=parser.getAttributeValue(null, "id");
                    Style newStyle = readStyle(parser);
                    styleList.put(id, newStyle);

                }
                else {

                    if ("bind".equals(name)) {

                        String style=parser.getAttributeValue(null, "style");
                        String key=parser.getAttributeValue(null, "key");
                        if (".*".equals(key)) { key =""; }
                        
                        Style newStyle = (Style)styleList.get(style);
                        Style oldStyle = getStyle(key);

                        Style theStyle;
                        if (oldStyle!=null) {
                            theStyle = new Style();
                            theStyle.putAll(oldStyle);
                            theStyle.putAll(newStyle);
                        }
                        else {
                            theStyle = newStyle;
                        }

                        if ("".equals(key)){
                            defaultStyle = theStyle;
                        }
                        
                        setStyleFor(key,theStyle);

                    }
                    else {
                        //#debug
                        System.out.println("unknown found: "+name);
                    }
                    
                    // read end tag
                    parser.skipSubTree();
                    
                }


            }

            fonts = null;

        }
        
        
        
        
        private Style readStyle(KXmlParser parser) throws Exception {
            
            Hashtable params = new Hashtable();
            
            Style newStyle;
            if (defaultStyle==null) {
                newStyle = new Style();
            }
            else {
                newStyle = new Style(defaultStyle);
            }

            // vars local to this style
            EmptyBorder insets = null;
            
            // read start tag
            while (parser.nextTag() != KXmlParser.END_TAG) {
                    String name = parser.getName();

                    if ("state".equals(name)) {

                        int st = workOutState( parser.getAttributeValue(null, "value") );
                        
                        // vars local to this state
                        int borderfill=-1;
                        MatteBorder border = null;
                        
                        // read start tag
                        //parser.getEventType();
                        while (parser.nextTag() != KXmlParser.END_TAG) {
                            String name2 = parser.getName();
                            
                            if ("font".equals(name2)) {
                                newStyle.addFont(loadFont(parser,fonts),st);
                            }
                            else {
                                if ("imagePainter".equals(name2)) {
                                    String path = parser.getAttributeValue(null, "path");
                                    String sourceInsets = parser.getAttributeValue(null, "sourceInsets");
                                    String paintCenter = parser.getAttributeValue(null, "paintCenter");

                                    Image img = getImage( path );
                                    if (img!=null) {
                                        Icon activeimage = new Icon( img );
                                        String[] split = StringUtil.split(sourceInsets, ' ');
                                        border = new MatteBorder(activeimage,
                                                insets==null?0:insets.getTop(), insets==null?0:insets.getLeft(), insets==null?0:insets.getBottom(), insets==null?0:insets.getRight(),
                                                Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]), 
                                                "true".equals(paintCenter) || "Y".equals(paintCenter)
                                                , borderfill);
                                        newStyle.addBorder(border, st);
                                    }
                                }
                                else if ("property".equals(name2)) {
                                    String type = parser.getAttributeValue(null, "type");
                                    String key = parser.getAttributeValue(null, "key");
                                    String value = parser.getAttributeValue(null, "value");

                                    if ("integer".equals(type)) {
                                        int i = parseInt(value, 10);
                                        newStyle.addProperty(new Integer(i), key, st);
                                    }
                                    else if (type==null || "idref".equals(type)) {

                                        Object obj = params.get(value);
                                        if (obj!=null) {
                                            newStyle.addProperty(obj, key, st);
                                        }

                                    }

                                }
                                else if ("color".equals(name2)) {
                                    String cvalue = parser.getAttributeValue(null, "value");
                                    String type = parser.getAttributeValue(null, "type");
                                    String id = parser.getAttributeValue(null, "id");
                                    int color = -1;
                                    if (cvalue!=null) {
                                        color = parseInt(cvalue, 16);
                                    }
                                    if (type!=null) {
                                        if ("BACKGROUND".equals(type)) {
                                            newStyle.addBackground(color, st);
                                        }
                                        else if ("FOREGROUND".equals(type)) {
                                            newStyle.addForeground(color, st);
                                        }
                                        else if ("BORDERFILL".equals(type)) {
                                            if (border!=null) {
                                                border.setColor(color);
                                            }
                                            else {
                                                borderfill = color;
                                            }
                                        }
                                    }
                                    if (id!=null) {
                                        params.put(id, new Integer(color));
                                    }
                                }
                                else {
                                    //#debug
                                    System.out.println("unknown found: "+name2);
                                }

                                // read end tag
                                parser.skipSubTree();
                            }
                        }
                        
                    }
                    else if ("font".equals(name)) {
                            
                           loadFont(parser,fonts);
                    }
                    else {
                        
                        if ("insets".equals(name)) {
                                String top = parser.getAttributeValue(null, "top");
                                String left = parser.getAttributeValue(null, "left");
                                String bottom = parser.getAttributeValue(null, "bottom");
                                String right = parser.getAttributeValue(null, "right");
                                
                                insets = new EmptyBorder(Integer.parseInt(top), Integer.parseInt(left), Integer.parseInt(bottom), Integer.parseInt(right));
                                newStyle.addBorder(insets, Style.ALL);
                        }
                        else if ("imageIcon".equals(name)) {
                                String path = parser.getAttributeValue(null, "path");
                                String x = parser.getAttributeValue(null, "x");
                                String y = parser.getAttributeValue(null, "y");
                                String width = parser.getAttributeValue(null, "width");
                                String height = parser.getAttributeValue(null, "height");
                                String id = parser.getAttributeValue(null, "id");

                                Image newImage = getImage( path );
                                if (newImage!=null) {
                                    if (x!=null && y!=null && width!=null && height!=null) {
                                        newImage = Image.createImage( newImage, Integer.parseInt(x), Integer.parseInt(y), Integer.parseInt(width), Integer.parseInt(height), Sprite.TRANS_NONE);
                                    }
                                    params.put(id, new Icon(newImage) );
                                }

                        }
                        else if ("opaque".equals(name)) {

                                String value = parser.getAttributeValue(null, "value");
				if ("false".equals(value)) {
					newStyle.addBackground(-1, Style.ALL);
				}

			}
                        else {
                            //#debug
                            System.out.println("unknown found: "+name);
                        }
                        
                        // read end tag
                        parser.skipSubTree();
                    }

            }
            
            return newStyle;
            
        }



        private int workOutState(String value) {

            int result=Style.ALL;
            
            if (value==null) {
                return result;
            }
//            if (value.indexOf("ENABLED")!=-1) {
//                result |= Style.ENABLED;
//            }
            if (value.indexOf("DISABLED")!=-1) {
                result |= Style.DISABLED;
            }
            if (value.indexOf("FOCUSED")!=-1) {
                result |= Style.FOCUSED;
            }
            if (value.indexOf("SELECTED")!=-1) {
                result |= Style.SELECTED;
            }
            
            return result;

        }

        // TODO: support bitmap fonts
        private Font loadFont(KXmlParser parser,Hashtable params) throws Exception {

                Font font = null;

                String fontIdRef = parser.getAttributeValue(null, "idref");
                if (fontIdRef != null) {
                    font = (Font) params.get(fontIdRef);
                    if (font != null) {
                        parser.skipSubTree();
                        return font;
                    }
                }

                String fontName = parser.getAttributeValue(null, "name");
                String fontSize = parser.getAttributeValue(null, "size");
                String fontStyle = parser.getAttributeValue(null, "style");
                String fontId = parser.getAttributeValue(null, "id");

                String path = parser.getAttributeValue(null, "path");

//#debug
System.out.println("Loading font: name: "+fontName+", size: "+fontSize+", style: "+fontStyle+", id: "+fontId);

                int fname=javax.microedition.lcdui.Font.FACE_PROPORTIONAL;
                int fsize=javax.microedition.lcdui.Font.SIZE_MEDIUM;
                int fstyle=javax.microedition.lcdui.Font.STYLE_PLAIN;

                if ("PROPORTIONAL".equals(fontName)) {
                    fname=javax.microedition.lcdui.Font.FACE_PROPORTIONAL;
                }
                else if ("MONOSPACE".equals(fontName)) {
                    fname=javax.microedition.lcdui.Font.FACE_MONOSPACE;
                }
                else if ("SYSTEM".equals(fontName)) {
                    fname=javax.microedition.lcdui.Font.FACE_SYSTEM;
                }
                if (fontStyle!=null) {
                    if (fontStyle.indexOf("BOLD")!=-1) {
                        fstyle |= javax.microedition.lcdui.Font.STYLE_BOLD;
                    }
                    if (fontStyle.indexOf("ITALIC")!=-1) {
                        fstyle |= javax.microedition.lcdui.Font.STYLE_ITALIC;
                    }
                    if (fontStyle.indexOf("UNDERLINED")!=-1) {
                       fstyle |= javax.microedition.lcdui.Font.STYLE_UNDERLINED;
                    }
                }

                if ("SMALL".equals(fontSize)) {
                    fsize=javax.microedition.lcdui.Font.SIZE_SMALL;
                }
                else if ("MEDIUM".equals(fontSize)) {
                    fsize=javax.microedition.lcdui.Font.SIZE_MEDIUM;
                }
                else if ("LARGE".equals(fontSize)) {
                    fsize=javax.microedition.lcdui.Font.SIZE_LARGE;
                }

                Vector colors = new Vector();
                Vector images = new Vector();
                while (parser.nextTag() != KXmlParser.END_TAG) {
                    String name = parser.getName();
                    // TODO, load bitmap font settings here

                    if ("fontImage".equals(name)) {
                        String imagePath = parser.getAttributeValue(null, "path");
                        String imageColor = parser.getAttributeValue(null, "color");

                        Image img = getImage(imagePath);
                        if (img !=null) {
                            int color = 0; // default to black
                            if (imageColor!=null) {
                                color = parseInt(imageColor, 16);
                            }
                            colors.addElement(new Integer(color));
                            images.addElement(img);
                        }
                    }

                    //#debug
                    System.out.println("oooo: "+name);
                    
                    parser.skipSubTree();
                }
                if (path!=null) {
                    InputStream in = getResourceAsStream(path);
                    if (in!=null) {
                        int[] colorsArray = new int[colors.size()];
                        for (int c=0;c<colorsArray.length;c++) {
                            colorsArray[c] = ((Integer)colors.elementAt(c)).intValue();
                        }
                        Image[] imagesArray = new Image[images.size()];
                        images.copyInto(imagesArray);
                        font = Font.getFont(in,imagesArray,colorsArray);
                    }
                }

                if (font==null) {
                    font = new Font(fname, fstyle, fsize);
                }

                if (fontId!=null) {
                    params.put(fontId, font);
                }

                return font;

        }
        
    
}
