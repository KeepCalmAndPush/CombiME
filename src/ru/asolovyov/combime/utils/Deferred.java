/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.utils;

import ru.asolovyov.combime.api.ICancellable;
import ru.asolovyov.combime.api.ISubscriber;
import ru.asolovyov.combime.api.ISubscription;
import ru.asolovyov.combime.impl.Completion;
import ru.asolovyov.combime.impl.CurrentValueSubject;
import ru.asolovyov.combime.impl.Demand;
import ru.asolovyov.combime.impl.Publisher;
import ru.asolovyov.combime.impl.Sink;

/**
 *
 * @author Администратор
 */
public class Deferred extends Publisher {
    protected Task task;
    protected CurrentValueSubject subject = new CurrentValueSubject(null);
    private boolean wasTaskStarted = false;

    public Deferred(Task task) {
        this.task = task;

        task.subscribe(new Sink() {
            protected void onValue(Object value) {
                System.out.println(this.getClass().getName() + " onValue " + value);
                Deferred.this.task = null;
                Deferred.this.subject.sendValue(value);
            }

            protected void onCompletion(Completion completion) {
                Deferred.this.task = null;
                Deferred.this.subject.sendCompletion(completion);
            }
        });
    }

    public ICancellable subscribe(ISubscriber subscriber) {
        ICancellable subscription = subject.subscribe(subscriber);
        if (!wasTaskStarted) {
            this.task.run();
            wasTaskStarted = true;
        }
        
        return subscription;
    }

    public void subscriptionDidRequestValues(ISubscription subscription, Demand demand) {
        System.out.println(this.getClass().getName() + " subscriptionDidRequestValues");
        subject.subscriptionDidRequestValues(subscription, demand);
    }
}