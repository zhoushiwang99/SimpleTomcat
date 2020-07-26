package com.zsw.simpletomcat.util;


import com.zsw.simpletomcat.Lifecycle;
import com.zsw.simpletomcat.LifecycleEvent;
import com.zsw.simpletomcat.LifecycleListener;


public final class LifecycleSupport {



    public LifecycleSupport(Lifecycle lifecycle) {

        super();
        this.lifecycle = lifecycle;

    }


    private Lifecycle lifecycle = null;


    private LifecycleListener[] listeners = new LifecycleListener[0];

    public void addLifecycleListener(LifecycleListener listener) {

      synchronized (listeners) {
          LifecycleListener results[] =
            new LifecycleListener[listeners.length + 1];
          for (int i = 0; i < listeners.length; i++)
              results[i] = listeners[i];
          results[listeners.length] = listener;
          listeners = results;
      }

    }

    public LifecycleListener[] findLifecycleListeners() {

        return listeners;

    }

    public void fireLifecycleEvent(String type, Object data) {

        LifecycleEvent event = new LifecycleEvent(lifecycle, type, data);
        LifecycleListener interested[] = null;
        synchronized (listeners) {
            interested = (LifecycleListener[]) listeners.clone();
        }
        for (LifecycleListener lifecycleListener : interested) {
            lifecycleListener.lifecycleEvent(event);
        }

    }


    public void removeLifecycleListener(LifecycleListener listener) {

        synchronized (listeners) {
            int n = -1;
            for (int i = 0; i < listeners.length; i++) {
                if (listeners[i] == listener) {
                    n = i;
                    break;
                }
            }
            if (n < 0)
                return;
            LifecycleListener results[] =
              new LifecycleListener[listeners.length - 1];
            int j = 0;
            for (int i = 0; i < listeners.length; i++) {
                if (i != n)
                    results[j++] = listeners[i];
            }
            listeners = results;
        }

    }


}
