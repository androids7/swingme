package net.yura.blackberry.rim;

import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.FocusChangeListener;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.EmailAddressEditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BorderFactory;
import net.yura.blackberry.rim.Canvas;
import net.yura.blackberry.rim.Display;
import net.yura.blackberry.rim.MIDlet;
import net.yura.blackberry.rim.TextBox;
import net.yura.blackberry.rim.Canvas.CanvasManager;
import net.yura.blackberry.rim.TextBox.InputHelper;
import net.yura.blackberry.rim.TextBox.TextBoxNative;
import net.yura.mobile.gui.ChangeListener;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.border.Border;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.TextComponent;
import net.yura.mobile.gui.plaf.Style;

public class TextBox {

    public static Class inputHelperClass = TextBox.TextBoxDialog.class;

    String title;
    String text;
    int maxSize;
    int constraints;
    Vector commands = new Vector();
    CommandListener commandListener;

    public TextBox(String title, String text, int maxSize, int constraints) {
        this.title = title;
        this.text = text;
        this.maxSize = maxSize;
        this.constraints = constraints;
    }

    public String getTitle() {
        return title;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public String getString() {
        return text;
    }

    public int getConstraints() {
        return constraints;
    }

    public void addCommand(Command command) {
        commands.addElement(command);
    }

    public void setCommandListener(CommandListener textComponent) {
        commandListener = textComponent;
    }

    public void setInitialInputMode(String characterSubset) {
    }

    public void fireCommand(Command command) {
        if (commandListener!=null) {
            commandListener.commandAction(command, null);
        }
    }

    public interface InputHelper {
        public void start(TextBox tb,MIDlet midlet);
        public void stop();
        public void onDraw();
        public void onLayout();
        public boolean keyPressed(int midpKeyCode, boolean alreadyProcessed);
    }

    //public void open(MIDlet midlet) {
    //    InputHelper helper = new TextBoxDialog();
    //    helper.start(this,midlet);
    //}

    public static class TextBoxDialog implements InputHelper {

        public void start(final TextBox tb,MIDlet midlet) {

            final PopupScreen popup = new PopupScreen(new VerticalFieldManager(),PopupScreen.DEFAULT_CLOSE) {
                protected boolean keyDown(int keyCode, int time) {
                    if (Keypad.key(keyCode) == Keypad.KEY_ESCAPE) {
                        for (int c=0;c<tb.commands.size();c++) {
                            final Command command = (Command)tb.commands.elementAt(c);
                            if (command.getCommandType() == Command.CANCEL || command.getCommandType() == Command.BACK) {
                                tb.fireCommand(command);
                                break;
                            }
                        }
                        return true;
                    }
                    return super.keyDown(keyCode, time);
                }
            };

            final TextField editField = (TextField)getTextFiled(tb, false);

            popup.add(new LabelField(tb.title));
            popup.add(editField);

            HorizontalFieldManager commandsPanel = new HorizontalFieldManager(Field.FIELD_HCENTER);

            for (int c=0;c<tb.commands.size();c++) {

                final Command command = (Command)tb.commands.elementAt(c);

                ButtonField button = new ButtonField( command.getLabel() );
                button.setChangeListener(new FieldChangeListener() {
                    public void fieldChanged(Field field, int context) {

                        // set text back from text field
                        tb.text = editField.getText();

                        tb.fireCommand(command);
                    }
                });

                commandsPanel.add(button);
            }

            popup.add(commandsPanel);

            midlet.pushScreen(popup);


        }

        public void onDraw() { }
        public void onLayout() { }

        public void stop() {
            // hiding this screen is handled in "Display.setCurrent(Canvas)"
        }

        public boolean keyPressed(int midpKeyCode, boolean alreadyProcessed) {
            return false; // this will never happen, as when the dialog is open all events go to it
        }
    }

    public static class TextBoxKeyboard implements InputHelper {

        Canvas screen;
        TextBox textBox;

        public void start(TextBox tb, MIDlet midlet) {

            textBox = tb;

            screen = (Canvas)Display.getDisplay(midlet).getCurrent();

            // there does not seem to be a way to tell it what type of keyboard to open
            // the hack to get round this is to create a EditField and focus it, then open the keyboard, then hide the EditField
            if (net.rim.device.api.ui.VirtualKeyboard.isSupported()) {
                screen.getVirtualKeyboard().setVisibility( net.rim.device.api.ui.VirtualKeyboard.SHOW );
            }
        }


        public void onDraw() { }
        public void onLayout() { }

        public void stop() {
            if (net.rim.device.api.ui.VirtualKeyboard.isSupported()) {
                screen.getVirtualKeyboard().setVisibility( net.rim.device.api.ui.VirtualKeyboard.HIDE );
            }
        }

        public boolean keyPressed(int midpKeyCode,boolean processed) {

            if (midpKeyCode==Canvas.KEY_END) {

                boolean result = net.rim.device.api.ui.VirtualKeyboard.isSupported() && screen.getVirtualKeyboard().getVisibility()==net.rim.device.api.ui.VirtualKeyboard.SHOW;

                if (textBox.commandListener!=null) {
                    for (int c=0;c<textBox.commands.size();c++) {
                        Command command = (Command)textBox.commands.elementAt(c);
                        if (command.getCommandType()==Command.CANCEL) {
                            textBox.commandListener.commandAction(command, null); // will in turn call "setCurrent(desktoppane)" that will call "setInputHelper(null)" that will call "stop()"
                            break;
                        }
                    }
                }

                if (result) {
                    return true; // if we have actually dismissed the keyboard, then consume the event and return true
                }
            }

            screen.sendKeyPressed(midpKeyCode);
            return true;
        }
    }

    public static class TextBoxNative implements InputHelper,ChangeListener, FieldChangeListener, FocusChangeListener {

        public static final ChangeListener starter = new ChangeListener() {
            public void changeEvent(Component source, int type) {
                if (type == net.yura.mobile.gui.components.Component.FOCUS_GAINED) {
                    ((TextComponent)source).openNativeEditor();
                }
            }
        };

        public static void init() {
            inputHelperClass = TextBoxNative.class;
            TextComponent.staticFocusListener = starter;
        }

        Canvas screen;
        Field editField;
        net.yura.mobile.gui.components.Component textField;
        TextBox textBox;

        public void start(final TextBox tb, MIDlet midlet) {

            textBox = tb;

            screen = (Canvas)Display.getDisplay(midlet).getCurrent();

            final net.yura.mobile.gui.components.Window window = net.yura.mobile.gui.DesktopPane.getDesktopPane().getSelectedFrame();
            textField = window.getFocusOwner();

            boolean singleLine = (textField instanceof net.yura.mobile.gui.components.TextField);

            editField = getTextFiled(tb, singleLine);

            editField.setFont( ((TextComponent)textField).getFont().getFont().font );

            swing2bb();

            Border insets = textField.getInsets();
            editField.setBorder(BorderFactory.createSimpleBorder(new XYEdges(insets.getTop(), insets.getRight(), insets.getBottom(), insets.getLeft()), net.rim.device.api.ui.decor.Border.STYLE_TRANSPARENT));

            // the swing margin is the BB padding, its what goes between the content and the border
            int swingMargin = ((TextComponent)textField).getMargin();
            getTextField(editField).setPadding(swingMargin, swingMargin, swingMargin, swingMargin);

            textField.setForeground( 0x00FFFFFF ); // stops the swingme component from drawing anything

            screen.add(editField);

            //onDraw(); // do not need to call this here, as we will call InputHelper.onLayout() and that will call the InputHelper.onDraw()

            TextComponent.staticFocusListener = this;

            editField.setFocus();



            getTextField(editField).setChangeListener(this);
            getTextField(editField).setFocusListener(this);

        }

        public void fieldChanged(Field field, int context) {
            bb2swing();
        }

        public void focusChanged(Field field, int eventType) {
            if (eventType == FocusChangeListener.FOCUS_CHANGED) { // caret changed
                bb2swing();
            }
        }

        private static TextField getTextField(Field field) {
        	if (field instanceof HorizontalFieldManager) {
        		return (TextField) ((HorizontalFieldManager)field).getField(0);
        	}
        	return (TextField)field;
        }

        public void changeEvent(Component source, int num) {
            if (num==Component.FOCUS_LOST) {

                TextComponent.staticFocusListener = starter;

                bb2swing();

                textField.setPreferredSize(textField.getPreferredWidth(), -1);

                textField.setForeground(Style.NO_COLOR);

                screen.setFocus(); // if we remove a field that currently has focus, we risk the caret staying floating over the top of the screen, so we first give focus to something else
                Field field = editField;
                editField=null;
                screen.delete(field);

                DesktopPane.mySizeChanged(textField);


            }
        }

        void bb2swing() {

            TextField field = getTextField(editField);

            // bb2midp
            String new1 = field.getText();
            if (!new1.equals(textBox.text)) {
                textBox.text = new1;
            }

            // midp2swingme
            String new2 = textBox.getString();
            if (!new2.equals( ((TextComponent)textField).getText() )) {
                ((TextComponent)textField).setText( new2 );
            }

            int caret = field.getCursorPosition();
            if (caret != ((TextComponent)textField).getCaretPosition()) {
                ((TextComponent)textField).setCaretPosition( caret );
            }
        }

        void swing2bb() {
            getTextField(editField).setCursorPosition( ((TextComponent)textField).getCaretPosition() );
        }

        public void onLayout() {

            // as this get called during the Canvas.add(Field) and we do not yet know the width to use for wraping, we need to call ondraw
            onDraw();

            if (editField!=null) {

                int swingMargin = ((TextComponent)textField).getMargin();

                // even though this looks like its doing nothing we NEED to call this as
                // it recalcs the ContentHeight based on the width you pass in and the text in the textbox
                CanvasManager man = (CanvasManager)screen.getDelegate();
                man.layoutChild2(editField, editField.getWidth(), editField.getHeight());

                int preferredHeight = getTextField(editField).getContentHeight() + swingMargin*2;

                if (preferredHeight != textField.getPreferredHeight()) {
                    textField.setPreferredSize(textField.getPreferredWidth(), preferredHeight);
                    DesktopPane.mySizeChanged(textField);
                }

                System.out.println("==================== Changing swingme textField size to: ContentHeight=" +preferredHeight + " height=" + editField.getHeight() + " PreferredHeight=" + editField.getPreferredHeight());

            }

        }

        public void onDraw() {

            if (editField!=null) {

                Border insets = textField.getInsets();

                int x = textField.getXOnScreen()-insets.getLeft();
                int y = textField.getYOnScreen()-insets.getTop();
                int w = textField.getWidthWithBorder();
                int h = textField.getHeightWithBorder();

                CanvasManager man = (CanvasManager)screen.getDelegate();

                if (x!=editField.getLeft() || y!= editField.getTop()) {
                    man.setPositionChild2(editField, x, y);
                }
                if (w!=editField.getWidth() || h!= editField.getHeight()) {

                    // for single line text boxes we want to center the text in the middle vertically
                    if (textField instanceof net.yura.mobile.gui.components.TextField) {
                        Field f = getTextField(editField);
                        int ph = f.getPreferredHeight() + editField.getBorder().getTop() + f.getPaddingTop() +  editField.getBorder().getBottom() + f.getPaddingBottom();
                        System.out.println("==================== The preferred height of the editfield is: " + ph );
                        if (h > ph) {
                            int toPad = (h - ph) / 2;
                            f.setPadding(f.getPaddingTop()+toPad, f.getPaddingRight(), f.getPaddingBottom()+toPad + ((h-ph)%2==0?0:1), f.getPaddingLeft());
                            System.out.println("==================== Padding editfield top and bottom with: " + toPad);
                        }
                    }

                    System.out.println("==================== Laying out editField with h=" + h + " and w=" + w);
                    man.layoutChild2(editField, w, h);
                    System.out.println("==================== Actual values after layout h=" + editField.getHeight() + " and w=" + editField.getWidth());
                }
            }
        }

        public void stop() {
            back(); // when we change window, this gets called to close the on-screen keyboard
        }

        // hide the keyboard if it is visible
        public boolean back() {
            if (net.rim.device.api.ui.VirtualKeyboard.isSupported()) { // we need to do this check or getVirtualKeyboard will return null
                net.rim.device.api.ui.VirtualKeyboard keyboard  = screen.getVirtualKeyboard();
                if (keyboard.getVisibility()==net.rim.device.api.ui.VirtualKeyboard.SHOW) {
                    keyboard.setVisibility( net.rim.device.api.ui.VirtualKeyboard.HIDE );
                    return true;
                }
            }
            return false;
        }

        public boolean keyPressed(int midpKeyCode,boolean processed) {

            if (midpKeyCode==Canvas.KEY_END) {
                boolean result = back(); // we do not want to shut down the inputhelper, just dismiss the keyboard, in TextBoxKeyboard we shut down the inputhelper in this event
                if (result) {
                    return true; // if we have actually dismissed the keyboard, then return true
                }
            }

            if (editField!=null) {

                    Button b = DesktopPane.getDesktopPane().getSelectedFrame().findMnemonicButton(midpKeyCode);

                    if (b!=null) {
                        bb2swing();

                        screen.sendKeyPressed(midpKeyCode); // process the event as normal

                        textBox.text = ((TextComponent)textField).getText(); // swing2midp
                        getTextField(editField).setText( textBox.text ); // midp2bb

                        swing2bb(); // update caret position

                    }
                    else {
                        // we have no button to trigger, we just want the event to be sent to the native component
                        // HACK, as bb returns false instead of true when a child handles a key event, as need to return here if a native child is open
                        return processed;
                    }
            }
            // if we have no edit field open just do what we normally do, pass the event to the canvas
            else {
                screen.sendKeyPressed(midpKeyCode);
            }

            return true; // default consume all events as we have dealt with everything

        }


    }

    private static Field getTextFiled(TextBox tb, boolean singleLine) {

        final TextField editField;

        int input = (tb.constraints & javax.microedition.lcdui.TextField.CONSTRAINT_MASK);
        long style;

        switch (input) {
            case javax.microedition.lcdui.TextField.DECIMAL: style = BasicEditField.FILTER_REAL_NUMERIC; break;
            case javax.microedition.lcdui.TextField.NUMERIC: style = BasicEditField.FILTER_INTEGER; break;
            case javax.microedition.lcdui.TextField.PHONENUMBER: style = BasicEditField.FILTER_PHONE; break;
            case javax.microedition.lcdui.TextField.EMAILADDR: style = BasicEditField.FILTER_EMAIL; break;
            case javax.microedition.lcdui.TextField.URL: style = BasicEditField.FILTER_URL; break;
            //case javax.microedition.lcdui.TextField.ANY: // fall though to default
            default: style = BasicEditField.FILTER_DEFAULT; break;
        }

        if (singleLine) {
        	style |=  EditField.NO_NEWLINE;
        }

        if (style == BasicEditField.FILTER_EMAIL) {
            editField = new EmailAddressEditField("","");
        }
        else if ((javax.microedition.lcdui.TextField.PASSWORD & tb.constraints) != 0) {
            editField = new PasswordEditField();
        }
        else {
            editField = new EditField(style);
        }

        editField.setText(tb.text);
        editField.setMaxSize(tb.maxSize);

	if (singleLine) {
	    HorizontalFieldManager man = new HorizontalFieldManager(HorizontalFieldManager.HORIZONTAL_SCROLL);
	    man.add(editField);
	    return man;
	}
	else {
	    return editField;
	}

    }

}
