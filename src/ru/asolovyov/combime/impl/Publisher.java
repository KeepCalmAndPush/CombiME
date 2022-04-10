/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.impl;

import java.util.Vector;
import ru.asolovyov.combime.api.ISubscriber;
import ru.asolovyov.combime.api.ICancellable;
import ru.asolovyov.combime.api.IPublisher;
import ru.asolovyov.combime.api.ISubscription;

/**
 *
 * @author Администратор
 */
public abstract class Publisher implements IPublisher {
    protected Vector subscriptions = new Vector();

    protected abstract ISubscription createSubscription(ISubscriber subscriber);

    public ICancellable subscribe(ISubscriber subscriber) {
        ISubscription subscription = createSubscription(subscriber);
        subscriber.receiveSubscription(subscription);
        subscriptions.addElement(subscription);
        return subscription;
    }
}
