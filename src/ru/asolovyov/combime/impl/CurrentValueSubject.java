/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.impl;

import ru.asolovyov.combime.api.ICancellable;
import ru.asolovyov.combime.api.ISubject;
import ru.asolovyov.combime.api.ISubscriber;
import ru.asolovyov.combime.api.ISubscription;

/**
 *
 * @author Администратор
 */
public class CurrentValueSubject extends PassthroughSubject implements ISubject {
    private Object currentValue;

    public CurrentValueSubject(Object currentValue) {
        this.currentValue = currentValue;
    }
    
    public void sendValue(Object value) {
        currentValue = value;
        super.sendValue(currentValue);
    }

    public void subscriptionDidRequestValues(ISubscription subscription, Demand demand) {
        subscription.getSubscriber().receiveInput(currentValue);
    }
}