/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.impl.subject;

import ru.asolovyov.combime.api.ISubscriber;
import ru.asolovyov.combime.impl.Completion;
import ru.asolovyov.combime.impl.Subscription;

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
        notifyNextValueAvailable();
        passInputToSubscriber();
    }

    protected Object emitValue() {
        return value;
    }

    protected Completion emitCompletion() {
        return new Completion(true, null);
    }
}
