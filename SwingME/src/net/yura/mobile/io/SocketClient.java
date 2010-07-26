package net.yura.mobile.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import net.yura.mobile.gui.Midlet;
import net.yura.mobile.logging.Logger;
import net.yura.mobile.util.QueueProcessorThread;

/**
 * @author Yura Mamyrin
 */
public abstract class SocketClient implements Runnable {
    public final static int DISCONNECTED = 1;
    public final static int CONNECTED = 2;
    public final static int CONNECTING = 3;
    public final static int COMMUNICATING = 4;
    public final static int DISCONNECTED_AND_PAUSED = 5;

    protected QueueProcessorThread writeThread;
    private Thread readThread;

    protected Vector offlineBox = new Vector();

    protected StreamConnection conn;
    protected OutputStream out;
    protected InputStream in;

    //#debug debug
    private boolean disconnected = false;


    // configurable parameters
    protected int maxRetries = 3;
    protected int retryWaitMultiplier = 2;
    protected int initialWaitValue = 1000;
    protected int maxWaitTimeMillis = 30000;
    boolean pauseReconnectOnFailure = false;

    private int retryCount = 0;
    protected String protocol = "socket://";
    private final String server;

    /**
     * you can pass null into this method, but then you need to Override {@link #getNextServer()}
     * @param server
     */
    public SocketClient(String server) {
        this.server = server;
    }

    public SocketClient(String server, int maxRetries, int retryWaitMultiplier, int initialWaitValue, int maxWaitTimeMillis, boolean pauseReconnectOnFailure){

        this.server = server;

        this.maxRetries = maxRetries;
        this.retryWaitMultiplier = retryWaitMultiplier;
        this.initialWaitValue = initialWaitValue;
        this.maxWaitTimeMillis = maxWaitTimeMillis;
        this.pauseReconnectOnFailure = pauseReconnectOnFailure;
    }


    protected StreamConnection openConnection(String serv) throws IOException {
    	
    	// TODO NOT DONE this needs to be turned ON for HTTP and turned OFF when used for BT
        if (Midlet.getPlatform() == Midlet.PLATFORM_BLACKBERRY &&
    		serv.indexOf(";") < 0) {
            // TODO: Hack for black berry direct tcp/ip connection
            // TODO: http://www.localytics.com/blog/post/how-to-reliably-establish-a-network-connection-on-any-blackberry-device/
        	serv += ";deviceside=true";
        }
    	
        return (StreamConnection)Connector.open(protocol + serv);
    }
    
    protected String getNextServer() {
        return server;
    }

    public void addToOutbox(Object obj) {

        if (writeThread==null) {
            writeThread = new QueueProcessorThread("[SocketClient] writeThread") {
                public void process(Object object) {

                    if (conn==null) {

                        int wait = initialWaitValue;
                        // MAKE THE CONNECTION!!
                        while (out==null || in==null) {

                            if(!isRunning()) return;

                            try {
                                retryCount++;

                                String serv = getNextServer();
                                //#debug info
                                Logger.info("[SocketClient] Trying to connect to: "+serv);

                                //#debug debug
                                if (disconnected) throw new IOException();

                                conn = openConnection(serv);
                                out = conn.openOutputStream();
                                in = conn.openInputStream();


                                // success therefore reset connecting state
                                updateState(CONNECTING);

                                // reset the retryCount and initial wait time
                                retryCount=0;
                                wait = initialWaitValue;
                            }
                            catch (SecurityException s) {
                                updateState(DISCONNECTED);
                                //#debug info
                                Logger.info(s);

                                securityException();
                                return;
                            }
                            catch (Exception x) {
                            	//#debug info
                                Logger.info(x);

                                if (pauseReconnectOnFailure && retryCount > maxRetries) {
                                    updateState(DISCONNECTED_AND_PAUSED);
                                    // number of reconnects exhausted so wait thread
                                    synchronized(this) {
                                        try {
                                            wait();
                                        }
                                        catch (Exception e){
                                        	//#debug info
                                            Logger.info(e);
                                        }
                                    }

                                    // update listener with current state
                                    updateState(DISCONNECTED);

                                    // reset the retry count
                                    retryCount = 0;
                                }
                                else {
                                    updateState(DISCONNECTED);

                                    try {
                                        // pause a while before next reconnect attempt
                                        Thread.sleep(wait);

                                        // increment the wait time between subsequent reconnect attempts
                                        wait = wait * retryWaitMultiplier;
                                        if (wait > maxWaitTimeMillis) {
                                            wait = maxWaitTimeMillis;
                                        }
                                    }
                                    catch (InterruptedException ex) {
                                    	//#debug info
                                      Logger.info(ex);
                                    }
                                }
                            }
                        }

                        // need to make a new 1, so any bits of xml still left in the old 1 are cleared
                        connected(in,out);

                        // START THE READ THREAD
                        readThread = new Thread(SocketClient.this,"[SocketClient] readThread");
                        readThread.start();

                    }

                    try {
			//#debug info
			Logger.info("[SocketClient] sending object: "+object);
                        updateState(COMMUNICATING);

                        //#debug debug
                        if (disconnected) throw new IOException();

                        write(out, object);

                        out.flush();

                        updateState(CONNECTED);

                    }
                    catch(Exception ex) {
                        // THIS WILL ONLY HAPPEN IF YOU GET A DISCONNECT DURING A SEND
                        //#mdebug info
                        Logger.info("[SocketClient] Exception during a write to socket");
                        Logger.info(ex);
                        //#enddebug

                        // move this and any queued objects to offline inbox
                        addToOfflineBox( object );

                        shutdownConnection();

                    }
                }
            };
            writeThread.start();
        }

        writeThread.addToInbox( obj );

    }

    public void addToOfflineBox(Object t) {
        if (!offlineBox.contains(t)) {
            offlineBox.addElement(t);
        }

        // if we are in the DISCONNECTED_AND_PAUSED state, wake us up
        synchronized(writeThread){
            writeThread.notify();
        }
    }

    public Vector getOfflineBox() {
        return offlineBox;
    }

    private synchronized void shutdownConnection() {

         NativeUtil.close(conn);
         conn = null;

         // we HAVE to close these here, as if we do not close them, and ONLY
         // close the Connection, the readThread stays in the blocked state
        NativeUtil.close(in); // close on input can block???
        NativeUtil.close(out);
        in = null;
        out = null;

        // move anything in the inbox to the offline inbox
        if (writeThread != null){
            Vector inbox = writeThread.getInbox();
            for (int c=0;c<inbox.size();c++) {
                addToOfflineBox(inbox.elementAt(c));
            }
            writeThread.clearInbox();
        }
    }


    public final void run() {
        try {
            //#mdebug info
            String name = readThread.getName();
            Logger.info("[SocketClient] STARTING "+ name );
            //#enddebug
            Thread.currentThread().setPriority( Thread.MIN_PRIORITY );
            Object task;
            while (true) {
                try {
              
               
                    //#debug debug
                    if (disconnected) throw new IOException();
                    
                    task = read(in);
                }
                catch(Exception ex) {
                    // THIS HAPPENS ON A DISCONNECT

                    updateState(DISCONNECTED);

                    //#mdebug info
                    if (writeThread!=null) {
                        Logger.info("[SocketClient] Exception during read from socket");
                        Logger.info(ex);
                    }
                    //#enddebug

                    shutdownConnection();

                    break;
                }

                updateState(COMMUNICATING);

              //#debug info
              Logger.info("[SocketClient] got object: "+task);
                try {

                    Thread.yield();
                    Thread.sleep(0);

                    handleObject( task );

                    Thread.yield();
                    Thread.sleep(0);

                }
                catch (Exception x) {
                    //#debug warn
                    Logger.warn("[SocketClient] CAN NOT HANDLE! Task: "+task+" "+x.toString() );
                    //#debug error
                    Logger.error(x);
                }
                //#debug info
                Logger.info("[SocketClient] finished handling object, waiting for new object from server");
                updateState(CONNECTED);
            }

            // we have not had a disconnect requested, so we have to get ready to reconnect
            if (writeThread!=null) {
                disconnected();
            }

            //#debug info
            Logger.info("[SocketClient] ENDING "+ name );
        }
        catch (Throwable t){
        	//#debug error
            Logger.error(t);
        }
    }

    public void disconnect() {
        shutdownConnection();
        if(writeThread != null) {
        	writeThread.kill();
        	writeThread = null;
        }
    }

    protected void sendOfflineInboxMessages() {
        //#debug info
        Logger.info("[SocketClient] sending offline messages: "+offlineBox);
        while (!offlineBox.isEmpty()) {
                Object task = offlineBox.elementAt(0);
                offlineBox.removeElementAt(0);
                addToOutbox( task );
        }
    }

    //#mdebug debug
    public void setDisconnected(boolean b){
        this.disconnected = b;
    }
    //#enddebug

    protected void securityException() {
        //#debug warn
        Logger.warn("[SocketClient] Socket connections are not allowed.");
    }

    public void setRetryCount(int retryCount){
        this.retryCount = retryCount;
    }

    protected int getRetryCount(){
        return retryCount;
    }

    protected int getMaxRetries(){
        return maxRetries;
    }

    /**
     * this is called when a object is recieved from the server
     */
    protected abstract void handleObject(Object task);

    /**
     * this is used to update a connection indicator
     */
    protected abstract void updateState(int c);

    protected abstract void write(OutputStream out, Object object) throws IOException;
    protected abstract Object read(InputStream in) throws IOException;

    /**
     * this method is called when a connection is established
     */
    protected abstract void connected(InputStream in,OutputStream out);

    /**
     * This method is called when a connection is lost
     *
     * this should send a new message if you want to make a connection again
     * the message you send will be something like 'hello' or 'login'
     */
    protected abstract void disconnected();

}