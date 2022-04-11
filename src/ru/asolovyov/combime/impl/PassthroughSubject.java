/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.impl;

import java.util.Enumeration;
import ru.asolovyov.combime.api.ISubject;
import ru.asolovyov.combime.api.ISubscriber;
import ru.asolovyov.combime.api.ISubscription;

/**
 *
 * @author Администратор
 */
public class PassthroughSubject extends Publisher implements ISubject {
    public void sendValue(Object value) {
        Enumeration elements = subscriptions.elements();
        while (elements.hasMoreElements()) {
            Subscription element = (Subscription)elements.nextElement();
            element.sendValue(value);
        }
    }

    public void sendCompletion(Completion completion) {
        Enumeration elements = subscriptions.elements();
        while (elements.hasMoreElements()) {
            Subscription element = (Subscription)elements.nextElement();
            element.sendCompletion(completion);
        }
        subscriptions.removeAllElements();
    }

    protected ISubscription createSubscription(ISubscriber subscriber) {
        Subscription subscription = new Subscription(subscriber);
        return subscription;
    }
}