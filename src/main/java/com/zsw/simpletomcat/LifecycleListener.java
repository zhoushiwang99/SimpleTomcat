package com.zsw.simpletomcat;


public interface LifecycleListener {


    /**
     * 当某个事件监听器监听到相关事件时，会调用该方法
     * @param event
     */
    public void lifecycleEvent(LifecycleEvent event);


}
