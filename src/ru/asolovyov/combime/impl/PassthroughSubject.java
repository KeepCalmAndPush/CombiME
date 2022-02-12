/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.impl;

import java.util.Enumeration;
import java.util.Vector;
import ru.asolovyov.combime.api.ISubject;
import ru.asolovyov.combime.api.ISubscriber;
import ru.asolovyov.combime.api.ISubscription;

/**
 *
 * @author Администратор
 */
public class PassthroughSubject extends Publisher implements ISubject {
    private Vector subscriptions = new Vector();

    public void sendValue(Object value) {
        Enumeration elements = subscriptions.elements();
        while (elements.hasMoreElements()) {
            SubjectSubscription element = (SubjectSubscription)elements.nextElement();
            element.sendValue(value);
        }
    }

    protected ISubscription createSubscription(ISubscriber subscriber) {
        SubjectSubscription subscription = new SubjectSubscription(subscriber);
        subscriptions.addElement(subscription);
        return subscription;
    }
}