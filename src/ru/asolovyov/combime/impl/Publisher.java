/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.impl;

import java.util.Vector;
import ru.asolovyov.combime.api.ISubscriber;
import ru.asolovyov.combime.api.ICancellable;
import ru.asolovyov.combime.api.IOperator;
import ru.asolovyov.combime.api.IPublisher;
import ru.asolovyov.combime.api.ISubscription;

/**
 *
 * @author Администратор
 */
public abstract class Publisher implements IPublisher {
    protected Vector subscriptions = new Vector();

    protected ISubscription createSubscription(ISubscriber subscriber) {
        Subscription subscription = new Subscription(subscriber);
        subscription.setDelegate(this);
        return subscription;
    }

    public ICancellable subscribe(ISubscriber subscriber) {
        ISubscription subscription = createSubscription(subscriber);
        subscriber.receiveSubscription(subscription);
        subscriptions.addElement(subscription);
        return subscription;
    }

    public IPublisher to(IOperator operator) {
        System.out.println("PUB TO " + operator);
        subscribe(operator);
        return operator;
    }

    public String toString() {
        return super.toString() + " count " + subscriptions.size();
    }

    public void subscriptionDidRequestValues(ISubscription subscription, Demand demand) { }

    public void subscriptionDidCancel(ISubscription subscription) {
        subscriptions.removeElement(subscription);
    }
}
