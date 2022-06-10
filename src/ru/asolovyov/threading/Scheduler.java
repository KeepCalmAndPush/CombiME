package ru.asolovyov.threading;

public interface Scheduler {
    public void schedule(Runnable runnable);
    public void schedule(long afterMillis, Runnable runnable);
}
