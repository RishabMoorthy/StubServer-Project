package org.framework.core;

public interface EventHandler {
    void onStart();
    void onStop();
    boolean onRequest();
    void onAfterRequest();
}
