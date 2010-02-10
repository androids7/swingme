package net.yura.mobile.util;

import java.util.Vector;

/**
 * @author Kat
 */
public abstract class QueueProcessorThread extends Thread {

    private Vector inbox = new Vector();
    private boolean runnning;

    public QueueProcessorThread() {
    }

    public QueueProcessorThread(String name) {
        super(name);
    }

    public void kill() {
           synchronized(this) {
                runnning = false;
                notify();
            }
    }

    public void run() {

        try {
            Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

            runnning = true;

            runLoop: while (runnning) {

                try {
                        Object object;

                        synchronized(this) {
                            while(inbox.isEmpty()) {
                                    if (!runnning) {
                                        break runLoop;
                                    }
                                    try {
                                        wait();
                                    }
                                    catch (InterruptedException ex) {
                                        ex.printStackTrace();
                                    }
                            }
                            object = inbox.elementAt(0);
                            inbox.removeElementAt(0);
                        }

                        // try not to slow down the UI
                        Thread.yield();
                        Thread.sleep(0);

                        process(object);

                        Thread.yield();
                        Thread.sleep(0);

                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    //DesktopPane.log(getName()+" "+ex.toString());
                }

            }

        }
        catch (Throwable t){
            t.printStackTrace();
        }

    }

    public void addToInbox(Object obj) {

        synchronized(this) {

            inbox.addElement(obj);
            notify();

        }

    }
    public boolean isRunning() {
        return runnning;
    }

    public void clearInbox() {
        synchronized(this) {
            inbox.removeAllElements();
        }
    }

    public Vector getInbox() {
        return inbox;
    }

    public abstract void process(Object object) throws Exception;

}
