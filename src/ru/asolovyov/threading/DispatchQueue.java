package ru.asolovyov.threading;

import java.util.Vector;

import ru.asolovyov.combime.common.S;

public final class DispatchQueue implements Scheduler {

    private Vector threads = new Vector();
    private Vector pendingRunnables = new Vector();
    private int busyThreadsCount = 0;
    private int idleThreadsCount = 0;
    private int numberOfConcurrentOperations = 0;
    private long threadIdlingTimeout = 5000;

    public DispatchQueue(int numberOfConcurrentOperations) {
        this(numberOfConcurrentOperations, 5000);
    }

    public DispatchQueue(int numberOfConcurrentOperations, long threadIdlingTimeout) {
        super();
        this.numberOfConcurrentOperations = numberOfConcurrentOperations;
        this.threadIdlingTimeout = threadIdlingTimeout;
    }

    public void schedule(Runnable runnable) {
        this.async(runnable);
    }

    public void schedule(long afterMillis, Runnable runnable) {
        this.async(afterMillis, runnable);
    }

    public synchronized void sync(Runnable runnable) {
        final Runnable fRunnable = runnable;

        Thread scheduler = new Thread(new Runnable() {

            public void run() {
                fRunnable.run();
                S.notify(fRunnable);
            }
        });
        scheduler.start();
        S.wait(fRunnable);
    }

    public void async(Runnable runnable) {
        S.debug("NEW RUNNABLE DISPATCHED!");
        this.pendingRunnables.addElement(runnable);
        this.spawnNewThreadIfNeeded();

        S.notify(this.pendingRunnables);
    }

    public void async(final long afterMillis, final Runnable runnable) {
        Runnable delayedRunnable = new Runnable() {

            public void run() {
                S.sleep(afterMillis);
                runnable.run();
            }
        };
        this.async(delayedRunnable);
    }

    private synchronized void spawnNewThreadIfNeeded() {
        S.debug("BT " + this.busyThreadsCount + " IT " + this.idleThreadsCount + " TS " + this.threads.size() + " NOCO " + this.numberOfConcurrentOperations);

        int threadsCount = this.busyThreadsCount + this.idleThreadsCount;

        if (threadsCount == this.threads.size()) {
            if (this.threads.size() < this.numberOfConcurrentOperations) {
                Thread thread = this.makeAThread();
                S.debug("SPAWN A THREAD! " + thread);
                this.threads.addElement(thread);
                this.idleThreadsCount++;
                thread.start();
            }
        }
    }

    private Thread makeAThread() {
        return new Thread(new Runnable() {

            public void run() {
                Runnable runnable = nextRunnableIfAny();
                if (runnable == null) {
                    S.wait(pendingRunnables, threadIdlingTimeout);
                    runnable = nextRunnableIfAny();
                }

                while (runnable != null) {
                    incrementBusyThreadsCount();
                    runnable.run();
                    decrementBusyThreadsCount();

                    runnable = nextRunnableIfAny();
                    if (runnable == null) {
                        S.wait(pendingRunnables, threadIdlingTimeout);
                        runnable = nextRunnableIfAny();
                    }
                }

                removeSelfFromThreads(Thread.currentThread());
            }
        });
    }

    private synchronized Runnable nextRunnableIfAny() {
        if (this.pendingRunnables.isEmpty()) {
            return null;
        }
        Runnable runnable = (Runnable) this.pendingRunnables.elementAt(0);
        this.pendingRunnables.removeElementAt(0);
        return runnable;
    }

    private synchronized void incrementBusyThreadsCount() {
        this.busyThreadsCount++;
        this.idleThreadsCount--;
    }

    private synchronized void decrementBusyThreadsCount() {
        this.busyThreadsCount--;
        this.idleThreadsCount++;
    }

    private synchronized void removeSelfFromThreads(Thread self) {
        S.debug("THREAD DISPOSED " + self);
        this.threads.removeElement(self);
    }
}
