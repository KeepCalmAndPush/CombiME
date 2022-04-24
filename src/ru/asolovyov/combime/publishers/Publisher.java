/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.publishers;

import java.util.Vector;
import ru.asolovyov.combime.api.ISubscriber;
import ru.asolovyov.combime.api.ICancellable;
import ru.asolovyov.combime.api.IOperator;
import ru.asolovyov.combime.api.IPublisher;
import ru.asolovyov.combime.api.ISubscription;
import ru.asolovyov.combime.api.ISubscriptionDelegate;
import ru.asolovyov.combime.api.Identifiable;
import ru.asolovyov.combime.common.Subscription;
import ru.asolovyov.combime.common.S;
import ru.asolovyov.combime.operators.Merge;

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

    public static IPublisher merge(IPublisher[] publishers) {
        if (publishers.length == 0) {
            return null;
        }

        if (publishers.length == 1) {
            return publishers[0];
        }

        IPublisher first = publishers[0];
        IPublisher[] others = new IPublisher[publishers.length - 1];
        for (int i = 1; i < publishers.length; i++) {
            others[i - 1] = publishers[i];
        }

        return first.to(new Merge(others));
    }
}