/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.asolovyov.combime.publishers;

import ru.asolovyov.combime.api.IOperator;
import ru.asolovyov.combime.api.IPublisher;
import ru.asolovyov.combime.api.ISubscriber;
import ru.asolovyov.combime.api.ISubscription;
import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.subjects.CurrentValueSubject;
import ru.asolovyov.combime.common.Demand;
import ru.asolovyov.combime.common.Sink;
import ru.asolovyov.combime.common.S;
import ru.asolovyov.combime.common.Task;

/**
 *
 * @author Администратор
 */
public class Deferred extends Publisher {

    protected Task task;
    protected CurrentValueSubject subject = new CurrentValueSubject(null);
    private boolean taskWasRun = false;

    public Deferred(Task task) {
        this.task = task;

        task.sink(new Sink() {

            protected void onValue(Object value) {
                S.debug("DEF TASK onValue " + value);
                Deferred.this.subject.sendValue(value);
                Deferred.this.task = null;
            }

            protected void onCompletion(Completion completion) {
                Deferred.this.subject.sendCompletion(completion);
                Deferred.this.task = null;
            }
        });
    }

    public ISubscription sink(ISubscriber subscriber) {
        ISubscription subscription = subject.sink(subscriber);
        runTaskIfNeeded();
        return subscription;
    }

    public IPublisher to(IOperator operator) {
        return subject.to(operator);
    }

    public void subscriptionDidRequestValues(ISubscription subscription, Demand demand) {
        S.debug("DEFERRED subscriptionDidRequestValues");
        super.subscriptionDidRequestValues(subscription, demand);
        if (taskWasRun) {
            subject.subscriptionDidRequestValues(subscription, demand);
        } else {
            runTaskIfNeeded();
        }
    }

    public void subscriptionDidCancel(ISubscription subscription) {
        super.subscriptionDidCancel(subscription);
        subject.subscriptionDidCancel(subscription);
    }

    private void runTaskIfNeeded() {
        if (!taskWasRun) {
            this.task.run();
            taskWasRun = true;
        }
    }
}
