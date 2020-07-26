
package com.zsw.simpletomcat;


public interface ClusterReceiver extends Runnable {


    public void setSenderId(String senderId);


    public String getSenderId();


    public void setDebug(int debug);

    public int getDebug();

    public void setCheckInterval(int checkInterval);


    public int getCheckInterval();


    public void setLogger(Logger logger);


    public Logger getLogger();

    public void log(String message);


    public Object[] getObjects();


    public void start();


    @Override
    public void run();


    public void stop();
}
