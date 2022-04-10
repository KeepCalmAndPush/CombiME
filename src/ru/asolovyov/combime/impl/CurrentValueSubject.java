/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.impl;

import ru.asolovyov.combime.api.ICancellable;
import ru.asolovyov.combime.api.ISubject;
import ru.asolovyov.combime.api.ISubscriber;

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

    public ICancellable subscribe(ISubscriber subscriber) {
        ICancellable subscription = super.subscribe(subscriber);
        if (currentValue != null) {
            ((Subscription)subscription).sendValue(currentValue);
        }
        return subscription;
    }
}