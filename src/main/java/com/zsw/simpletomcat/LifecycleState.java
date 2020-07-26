package com.zsw.simpletomcat;

public enum LifecycleState {
    NEW(false, (String)null),
    INITIALIZING(false, "before_init"),
    INITIALIZED(false, "after_init"),
    STARTING_PREP(false, "before_start"),
    STARTING(true, "start"),
    STARTED(true, "after_start"),
    STOPPING_PREP(true, "before_stop"),
    STOPPING(false, "stop"),
    STOPPED(false, "after_stop"),
    DESTROYING(false, "before_destroy"),
    DESTROYED(false, "after_destroy"),
    FAILED(false, (String)null);

    private final boolean available;
    private final String lifecycleEvent;

    LifecycleState(boolean available, String lifecycleEvent) {
        this.available = available;
        this.lifecycleEvent = lifecycleEvent;
    }

    public boolean isAvailable() {
        return this.available;
    }

    public String getLifecycleEvent() {
        return this.lifecycleEvent;
    }

}
