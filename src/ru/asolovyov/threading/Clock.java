/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.threading;

import java.util.Vector;
import ru.asolovyov.combime.common.S;
/**
 *
 * @author Администратор
 */
public class Clock {
    private Thread clockThread;
    private Thread workThread;
    private final int tickIntervalMillis;
    private Vector runnables = new Vector();
    private Vector onceRunnables = new Vector();

    public Clock(int tickIntervalMillis) {
        super();
        this.tickIntervalMillis = tickIntervalMillis;
        
        this.workThread = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        synchronized(workThread) {
                            workThread.wait();
                        }
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    
                    for (int i = 0; i < runnables.size(); i++) {
                        Runnable task = (Runnable) (runnables.elementAt(i));
                        task.run();
                    }

                    for (int i = 0; i < onceRunnables.size(); i++) {
                        Runnable task = (Runnable) (onceRunnables.elementAt(i));
                        remove(task);
                    }
                }
            }
        });
        
        this.clockThread = new Thread(new Runnable() {
            public void run() {
                while(true) {
                    synchronized(workThread) {
                        workThread.notify();
                    }
                    S.sleep(Clock.this.tickIntervalMillis);
                }
            }
        });

        this.workThread.start();
        this.clockThread.start();
    }

    public void add(Runnable runnable) {        
        this.runnables.addElement(runnable);
    }

    public void addOnce(Runnable runnable) {
        this.runnables.addElement(runnable);
        this.onceRunnables.addElement(runnable);
    }

    public void remove(Runnable runnable) {
        this.runnables.removeElement(runnable);
        this.onceRunnables.addElement(runnable);
    }
}
