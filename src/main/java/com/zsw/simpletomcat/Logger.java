package com.zsw.simpletomcat;


import java.beans.PropertyChangeListener;

/**
 * 日志记录器必须实现Logger接口
 */
public interface Logger {
    public static final int FATAL = Integer.MIN_VALUE;

    public static final int ERROR = 1;

    public static final int WARNING = 2;

    public static final int INFORMATION = 3;

    public static final int DEBUG = 4;

    public Container getContainer();

    public void setContainer(Container container);

    public String getInfo();

    public int getVerbosity();

    public void setVerbosity(int verbosity);

    public void addPropertyChangeListener(PropertyChangeListener listener);

    public void log(String message);

    public void log(Exception exception, String msg);

    public void log(String message, Throwable throwable);

    /**
     * 接收一个日志级别参数，如果参数的日志级别比该日志记录器实例设定的等级低，才会记录此条消息
     * @param message 日志信息
     * @param verbosity 日志级别参数
     */
    public void log(String message, int verbosity);

    public void log(String message, Throwable throwable, int verbosity);

    public void removePropertyChangeListener(PropertyChangeListener listener);


}
