package listeners;

import interfaces.ListenerCallbackInterface;

import java.util.ArrayList;
import java.util.List;

public class Listener {

    List<Thread> threads = new ArrayList<>();
    private static final int SLEEP = 10000;
    private boolean isRegistrationAvailable = true;

    protected void registerEvent(ListenerCallbackInterface callback) {
        Thread thread = new Thread(() -> {
            while (this.isRegistrationAvailable) {
                callback.call();
                try {
                    Thread.sleep(SLEEP);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        threads.add(thread);
    }

    public void unregisterEvents() {
        this.isRegistrationAvailable = false;
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        this.isRegistrationAvailable = true;
    }
}
