/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.impl;

import java.util.Enumeration;
import ru.asolovyov.combime.api.ISubject;
import ru.asolovyov.combime.api.ISubscription;

/**
 *
 * @author Администратор
 */
public class PassthroughSubject extends Publisher implements ISubject {
    public void sendValue(Object value) {
        Enumeration elements = subscriptions.elements();
        System.out.println(this.getId() + " PTS sendValue " + value);
        while (elements.hasMoreElements()) {
            Subscription element = (Subscription)elements.nextElement();
            System.out.println("to " + element.getSubscriber());
            element.sendValue(value);
        }
    }

    public void sendCompletion(Completion completion) {
        Enumeration elements = subscriptions.elements();
        System.out.println(this.getId() + " PTS Sending completion");
        while (elements.hasMoreElements()) {
            Subscription element = (Subscription)elements.nextElement();
            System.out.println(this.getId() + " PTS Sending completion to " + element);
            element.sendCompletion(completion);
        }
//        subscriptions.removeAllElements();
    }

    public void subscriptionDidRequestValues(ISubscription subscription, Demand demand) {
        System.out.println(this.getId() + " PTS subscriptionDidRequestValues(ISubscription subscription, Demand demand)");
    }
}