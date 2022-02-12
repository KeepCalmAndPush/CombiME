/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.impl;

import java.util.Enumeration;
import java.util.Vector;
import ru.asolovyov.combime.api.ICancellable;
import ru.asolovyov.combime.api.ISubject;
import ru.asolovyov.combime.api.ISubscriber;
import ru.asolovyov.combime.api.ISubscription;

/**
 *
 * @author Администратор
 */
public class CurrentValueSubject extends Publisher implements ISubject {
    private Object currentValue;
    private Vector subscriptions = new Vector();

    public CurrentValueSubject(Object currentValue) {
        this.currentValue = currentValue;
    }
    
    public void sendValue(Object value) {
        currentValue = value;
        notifySubscribers();
    }

    private void notifySubscribers() {
        Enumeration elements = subscriptions.elements();
        while (elements.hasMoreElements()) {
            SubjectSubscription element = (SubjectSubscription)elements.nextElement();
            element.sendValue(currentValue);
        }
    }

    protected ISubscription createSubscription(ISubscriber subscriber) {
        SubjectSubscription subscription = new SubjectSubscription(subscriber);
        return subscription;
    }

    public ICancellable receiveSubscriber(ISubscriber subscriber) {
        ISubscription subscription = createSubscription(subscriber);
        subscriptions.addElement(subscription);
        subscriber.receiveSubscription(subscription);
        ((SubjectSubscription)subscription).sendValue(currentValue);
        return subscription;
    }
}