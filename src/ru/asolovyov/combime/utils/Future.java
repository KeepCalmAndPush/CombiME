/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.utils;

import java.util.Enumeration;
import ru.asolovyov.combime.api.ICancellable;
import ru.asolovyov.combime.api.ISubject;
import ru.asolovyov.combime.api.ISubscriber;
import ru.asolovyov.combime.api.ISubscription;
import ru.asolovyov.combime.impl.Completion;
import ru.asolovyov.combime.impl.PassthroughSubject;
import ru.asolovyov.combime.impl.Publisher;
import ru.asolovyov.combime.impl.Sink;
import ru.asolovyov.combime.impl.Subscription;

/**
 *
 * @author Администратор
 */
public class Future extends Publisher {
    private Task task;
    private Object value;
    private Exception failure;

    public Future(Task task) {
        this.task = task;

        task.subscribe(new Sink() {
            protected void onValue(Object value) {
                Future.this.value = value;
                Future.this.task = null;
                Future.this.sendValue(value);
            }

            protected void onCompletion(Completion completion) {
                Future.this.failure = completion.getFailure();
                Future.this.task = null;
                Future.this.sendCompletion(completion);
            }
        });
        
        task.run();
    }

    protected ISubscription createSubscription(ISubscriber subscriber) {
        Subscription subscription = new Subscription(subscriber);
        return subscription;
    }

    public ICancellable subscribe(ISubscriber subscriber) {
        Subscription subscription = (Subscription) super.subscribe(subscriber);
        if (value != null) {
            subscription.sendValue(value);
//            subscriptions.removeElement(subscription);
        } else if (failure != null) {
            subscription.sendCompletion(new Completion(false, failure));
//            subscriptions.removeElement(subscription);
        }
        return subscription;
    }

    private void sendValue(Object value) {
        Enumeration elements = subscriptions.elements();
        while (elements.hasMoreElements()) {
            Subscription element = (Subscription)elements.nextElement();
            element.sendValue(value);
        }
    }

    private void sendCompletion(Completion completion) {
        Enumeration elements = subscriptions.elements();
        while (elements.hasMoreElements()) {
            Subscription element = (Subscription)elements.nextElement();
            element.sendCompletion(completion);
        }
//        subscriptions.removeAllElements();
    }
}


