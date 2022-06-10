/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.operators.reducing;

import java.util.Vector;

import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.common.Demand;
import ru.asolovyov.combime.common.S;
import ru.asolovyov.combime.operators.Operator;

/**
 *
 * @author Администратор
 */

public class Collect extends Operator {
    private int count = 0;
    private long millis = 0;

    private Vector collectedValues = new Vector();

    private Thread collectorThread;

    public Collect(int count) {
       this(0, count);
    }

    public Collect(long millis) {
       this(millis, 0);
    }

    public Collect(long millis, int count) {
        if (count <= 0 && millis <= 0) {
            throw new IllegalArgumentException("You MUST specify either count or millis, positive");
        }

        this.millis = millis;
        this.count = count;
    }

    protected Demand _receiveInput(Object input) {
        startCollectorThreadIfNeeded();
        
        collectedValues.addElement(input);
        
        if (count > 0 && collectedValues.size() == count) {
            sendCollectedAndReset();
        }

        return Demand.UNLIMITED;
    }

    public void receiveCompletion(Completion completion) {
        if (completion.isSuccess()) {
            sendCollectedAndReset();
        }
        super.receiveCompletion(completion);
    }

    private synchronized void sendCollectedAndReset() {
        if (collectedValues.isEmpty()) {
            return;
        }
        
        Object[] tail = new Object[collectedValues.size()];
        collectedValues.copyInto(tail);
        collectedValues.removeAllElements();
        
        sendValue(tail);
    }

    private synchronized void startCollectorThreadIfNeeded() {
        if (millis <= 0) {
            return;
        }
        
        if (subscription == null || subscriptions.isEmpty()) {
            return;
        }

        if (collectorThread != null && collectorThread.isAlive()) {
            return;
        }

        collectorThread = new Thread(new Runnable() {
            public void run() {
                S.sleep(millis);
                sendCollectedAndReset();
            }
        });

        collectorThread.start();
    }
}