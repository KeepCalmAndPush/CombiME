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
import ru.asolovyov.combime.operators.combining.CombineLatest;
import ru.asolovyov.combime.operators.combining.Merge;
import ru.asolovyov.combime.operators.combining.Zip;

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
        return new Merge(publishers);
    }

    public IPublisher merge(IPublisher publisher) {
        return new Merge(new IPublisher[]{this, publisher});
    }

    public static IPublisher combineLatest(IPublisher[] publishers) {
        return new CombineLatest(publishers);
    }

    public IPublisher combineLatest(IPublisher publisher) {
        return new CombineLatest(new IPublisher[]{this, publisher});
    }

    public static IPublisher zip(IPublisher[] publishers) {
        return new Zip(publishers);
    }

    public IPublisher zip(IPublisher publisher) {
        return new Zip(new IPublisher[]{this, publisher});
    }
}
