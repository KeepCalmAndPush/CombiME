package ru.asolovyov.threading;

import ru.asolovyov.combime.common.S;

public class DummyScheduler implements Scheduler {

    public void schedule(Runnable runnable) {
        runnable.run();
    }

    public void schedule(long afterMillis, Runnable runnable) {
        S.sleep(afterMillis);
        runnable.run();
    }
}
