/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.asolovyov.combime.operators.timing;

import ru.asolovyov.combime.api.ISubscriber;
import ru.asolovyov.combime.api.ISubscription;
import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.common.Demand;
import ru.asolovyov.combime.operators.Operator;
import ru.asolovyov.threading.DispatchQueue;
import ru.asolovyov.threading.Scheduler;

/**
 *
 * @author Администратор
 */
public class Delay extends Operator {

    private long delay = 0;
    private Scheduler scheduler;

    public Delay(long millis) {
        this(millis, new DispatchQueue(1));
    }

    public Delay(long millis, Scheduler scheduler) {
        super();
        this.delay = millis;
        this.scheduler = scheduler;
    }

    public Demand receiveInput(final Object input) {
        this.scheduler.schedule(this.delay, new Runnable() {

            public void run() {
                Delay.this.sendValue(input);
            }
        });

        return Demand.UNLIMITED;
    }

    public void receiveCompletion(final Completion completion) {
        this.scheduler.schedule(this.delay, new Runnable() {

            public void run() {
                Delay.this.sendCompletion(completion);
            }
        });
    }

    public ISubscription sink(ISubscriber subscriber) {
        return super.sink(subscriber);
    }
}
