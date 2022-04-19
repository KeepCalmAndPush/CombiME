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
import ru.asolovyov.combime.api.ISubscriptionDelegate;
import ru.asolovyov.combime.api.Identifiable;
import ru.asolovyov.combime.utils.S;

/**
 *
 * @author Администратор
 */
public abstract class Publisher implements IPublisher, ISubscriptionDelegate, Identifiable {
    protected Vector subscriptions = new Vector();

    private static long ID_COUNTER = 0;
    private long id;

    { generateId(); }

    private synchronized void generateId() {
        id = Publisher.ID_COUNTER++;
    }

    public long getId() {
        return id;
    }

    protected ISubscription createSubscription(ISubscriber subscriber) {
        Subscription subscription = new Subscription(subscriber);
        subscription.setDelegate(this);
        S.debug("Subscription created: " + subscription.getId());
        return subscription;
    }

    public ICancellable sink(ISubscriber subscriber) {
        ISubscription subscription = createSubscription(subscriber);
        subscriptions.addElement(subscription);
        subscriber.receiveSubscription(subscription);
        return subscription;
    }

    public IPublisher to(IOperator operator) {
        S.debug(this.getId() + " PUB TO " + operator.getId());
        sink(operator);
        return operator;
    }

    public String toString() {
        return super.toString() + " subscriptions: " + subscriptions.size();
    }

    public void subscriptionDidCancel(ISubscription subscription) {
        subscriptions.removeElement(subscription);
    }
}
