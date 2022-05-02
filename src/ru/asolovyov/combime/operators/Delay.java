/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.operators;

import ru.asolovyov.combime.api.ICancellable;
import ru.asolovyov.combime.api.ISubscriber;
import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.common.Demand;
import ru.asolovyov.combime.common.S;

/**
 *
 * @author Администратор
 */
public class Delay extends Operator {
    private long delay = 0;

    public Delay(long millis) {
        delay = millis;
}

    public Demand receiveInput(final Object input) {
        (new Thread(new Runnable() {
            public void run() {
                S.sleep(Delay.this.delay);
                Delay.this.sendValue(input);
            }
        })).start();

        return Demand.UNLIMITED;
    }

    public void receiveCompletion(final Completion completion) {
        (new Thread(new Runnable() {
            public void run() {
                S.sleep(Delay.this.delay);
                Delay.this.sendCompletion(completion);
            }
        })).start();
    }

    public ICancellable sink(ISubscriber subscriber) {
        return super.sink(subscriber);
    }
}
