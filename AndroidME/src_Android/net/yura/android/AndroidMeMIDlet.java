package net.yura.android;


import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;

import net.yura.android.lcdui.Toolkit;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class AndroidMeMIDlet extends Activity implements Toolkit, OnItemClickListener {

    private MIDlet midlet;
    private Vector<String[]> jadMidlets;
    private View defaultView;
    private View waitingView;
    private Handler handler;
    private InputMethodManager inputManager;
    private Thread eventThread;
    private Object lock = new Object();

    public static AndroidMeMIDlet DEFAULT_ACTIVITY;

    public AndroidMeMIDlet() {
        DEFAULT_ACTIVITY = this;
    }

    public MIDlet getMIDlet() {
        return this.midlet;
    }

    public Handler getHandler() {
        return handler;
    }

    public void invokeAndWait(final Runnable runnable) {
        if (Thread.currentThread() == this.eventThread) {
            runnable.run();
        } else {
            Runnable r = new Runnable() {
                public void run() {
                    synchronized (AndroidMeMIDlet.this.lock) {
                        runnable.run();
                        AndroidMeMIDlet.this.lock.notify();
                    }
                }
            };
            synchronized (this.lock) {
                this.handler.post(r);
                try {
                    this.lock.wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        this.handler = new Handler();
        this.eventThread = Thread.currentThread();

        showWaitingView(false);

        try {
            if (midlet == null) {
                startMIDlet(null);
            }
            else {
                midlet.doStartApp();
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

//        PrintStream log = new PrintStream(new LogOutputStream("AndroidMe"));
//        System.setErr(log);
//        System.setOut(log);
    }

    private void showContentView(final View view) {
        invokeAndWait(new Runnable() {
            public void run() {
                if (defaultView != view) {
                    setContentView(view);
                }
            }
        });
        this.defaultView = view;
    }

    private void showWaitingView(boolean wait) {
        if (waitingView == null) {
            waitingView = this.getLayoutInflater().inflate(R.layout.main, null, false);
        }

        showContentView(waitingView);

        if (wait) {
            while (defaultView.getWidth() == 0) {
                try {
                    System.out.println("Waiting for view...");
                    Thread.sleep(100);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void startMIDlet(final String midletClassName) {
        Thread thread = new Thread() {
            public void run() {
                try {
                    MIDlet midlet = createMIDlet(midletClassName);
                    AndroidMeMIDlet.this.midlet = midlet;

                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
        };

        thread.start();
    }

    private MIDlet createMIDlet(String midletClassName) {
        showWaitingView(false);

        Properties properties = new Properties();
        System.setProperty("microedition.platform", "microemulator-android");
        System.setProperty("microedition.locale", Locale.getDefault().toString());
        System.setProperty("microedition.configuration", "CLDC-1.1");
        System.setProperty("microedition.profiles", "MIDP-2.0");

        try {
            String[] assetList = getResources().getAssets().list("");
            for (int i = 0; i < assetList.length; i++) {
                System.out.println("> > >" + assetList[i]);
                if (assetList[i].endsWith(".jad")) {
                    System.out.println("Found a Jad File: " + assetList[i]);

                    InputStream is = getAssets().open(assetList[i]);
                    properties.load(is);
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        System.out.println(">>>1 " + System.getProperty("microedition.platform"));

        // midletClassName = "net.yura.mobile.test.MyMidlet";
        // midletClassName = "com.badoo.locator.BadooMidlet";
        // midletClassName = "net.java.dev.lwuit.speed.SpeedMIDlet";


        jadMidlets = new Vector<String[]>();
        if (midletClassName == null) {

            int count = 0;
            while (true) {
                count++;
                String midletProp = properties.getProperty("MIDlet-" + count);
                System.out.println("Found MIDlet: " + midletProp);
                if (midletProp == null) {
                    break;
                }

                try {
                    int firstComma = midletProp.indexOf(',');
                    int lastComma = midletProp.lastIndexOf(',');

                    String midletName = midletProp.substring(0, firstComma).trim();
                    String iconName   = midletProp.substring(firstComma + 1, lastComma).trim();
                    String className  = midletProp.substring(lastComma + 1).trim();

                    String[] midletEntry = {midletName, iconName, className};
                    jadMidlets.add(midletEntry);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

            if (jadMidlets.size() == 1) {
                midletClassName = jadMidlets.elementAt(0)[2];
            }
            else if (jadMidlets.size() > 1) {
                ListView listView = new ListView(this);
                listView.setOnItemClickListener(this);

                String[] listValues = new String[jadMidlets.size()];
                for (int i = 0; i < jadMidlets.size(); i++) {
                    listValues[i] = jadMidlets.elementAt(i)[0];
                }

                listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listValues));

                showContentView(listView);
            }
        }


        if (midletClassName != null) {
            showWaitingView(true);

            // create a new class loader that correctly handles getResourceAsStream!
            final ClassLoader classLoader = this.getClassLoader();

            MIDlet.DEFAULT_ACTIVITY = this;
            MIDlet.DEFAULT_TOOLKIT = this;
            MIDlet.DEFAULT_APPLICATION_PROPERTIES = properties;

            final String className = midletClassName;
            invokeAndWait(new Runnable() {
                public void run() {
                    try {
                        Class midletClass = Class.forName(className, true, classLoader);
                        midlet = (MIDlet) midletClass.newInstance();

                        if (midlet != null) {
                            midlet.doStartApp();
                        }
                    } catch (Exception ex) {
                        throw new RuntimeException("unable to load class "
                                + className, ex);
                    }
                }
            });
        }

        return midlet;
    }

// JP - TODO
//    @Override
//    protected void onDestroy() {
//        try {
//            if (this.midlet != null) {
//                this.midlet.doDestroyApp(true);
//                this.midlet = null;
//            }
//        } catch (Exception ex) {
//            throw new RuntimeException("unable to destroy", ex);
//        }
//        // this.resources.getAssets().release();
//        super.onDestroy();
//    }
//
//    @Override
//    protected void onPause() {
//        try {
//            if (this.midlet != null) {
//                this.midlet.doPauseApp();
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            throw new RuntimeException("unable to freeze app", ex);
//        }
//        super.onPause();
//    }
//
//    @Override
//    public void onStop() {
//        // TODO: JP
////JP        onDestroy();
//
//        super.onStop();
//    }
//
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        try {
//            if (midlet == null) {
//                startMIDlet(null);
//            }
//            else {
//                midlet.doStartApp();
//            }
//        } catch (Throwable ex) {
//            ex.printStackTrace();
//        }
//    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (midlet == null) {
            return super.onPrepareOptionsMenu(menu);
        }

        boolean result = false;
        Display display = Display.getDisplay(midlet);
        Displayable current = display.getCurrent();
        if (current != null) {
            // load the menu items
            menu.clear();
            Vector<Command> commands = current.getCommands();
            for (int i = 0; i < commands.size(); i++) {
                result = true;
                Command cmd = commands.get(i);
                if (cmd.getCommandType() != Command.BACK && cmd.getCommandType() != Command.EXIT) {
                    menu.addSubMenu(Menu.NONE, i + Menu.FIRST, Menu.NONE, cmd.getLabel());
                }
            }
        }
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Display display = Display.getDisplay(midlet);
        Displayable current = display.getCurrent();
        if (current != null) {
            Vector<Command> commands = current.getCommands();

            int commandIndex = item.getItemId() - Menu.FIRST;
            Command c = commands.get(commandIndex);
            CommandListener l = current.getCommandListener();

            if (c != null && l != null) {
                l.commandAction(c, current);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (this.midlet == null) {
            return super.onKeyDown(keyCode, event);
        }

        Displayable displayable = Display.getDisplay(this.midlet).getCurrent();

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // we need to see if there is a back option in the current display
            for (Command c : displayable.getCommands()) {
                if (c.getCommandType() == Command.BACK) {
                    // manually call the back operation: YECH!
                    displayable.getCommandListener().commandAction(c,
                            displayable);
                    return true;
                }
            }
        }

        if (displayable instanceof OnKeyListener) {
            OnKeyListener keyListener = (OnKeyListener) displayable;
            keyListener.onKey(null, keyCode, event);
        }

        return super.onKeyDown(keyCode, event);
    }

    public int getScreenHeight() {
        return this.defaultView.getHeight();
    }

    public int getScreenWidth() {
        return this.defaultView.getWidth();
    }

    public View inflate(int resourceId) {
        return this.getLayoutInflater().inflate(resourceId, null, false);
    }

    public static InputStream getResourceAsStream(Class origClass, String name)
             {

        long time = System.currentTimeMillis();
        System.out.println(">>> getResourceAsStream (" + origClass.getName() + ")" + name);
        long time2 = System.currentTimeMillis() - time;

        InputStream is = null;
        try {
            if (name.startsWith("/")) {
                name = name.substring(1);
            }

            is = DEFAULT_ACTIVITY.getAssets().open(name);
        } catch (Throwable e) {
            // e.printStackTrace();
        }

        if (is == null) {
            long elapsed = System.currentTimeMillis() - time;
            System.out.println(">>> getResourceAsStream: NOT FOUND. " + time2 + " " + elapsed);
        }

        return is;
    }

    //@Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (position >= 0) {
            String midletClassName = jadMidlets.elementAt(position)[2];
            startMIDlet(midletClassName);
        }
    }

    public void showNativeTextInput() {
        inputManager = (InputMethodManager) defaultView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        //inputManager.hideSoftInputFromWindow(defaultView.getWindowToken(), 0);

        inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

}