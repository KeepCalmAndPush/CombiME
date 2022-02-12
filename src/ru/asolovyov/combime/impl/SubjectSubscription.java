/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.impl;

import ru.asolovyov.combime.api.ISubscriber;

/**
 *
 * @author Администратор
 */
class SubjectSubscription extends Subscription {
    public SubjectSubscription(ISubscriber subscriber) {
        super(subscriber);
    }
    
    private Object value;
    
    public void sendValue(Object value) {
        this.value = value;
        hasNextValue = true;
        passInputToSubscriber();
    }

    protected Object emitValue() {
        return value;
    }

    protected Completion emitCompletion() {
        return new Completion(true, null);
    }
}
