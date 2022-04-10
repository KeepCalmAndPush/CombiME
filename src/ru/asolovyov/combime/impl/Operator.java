/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.impl;

import ru.asolovyov.combime.api.ICancellable;
import ru.asolovyov.combime.api.IOperator;
import ru.asolovyov.combime.api.ISubscriber;
import ru.asolovyov.combime.api.ISubject;

/**
 *
 * @author Администратор
 */
public abstract class Operator extends Subscriber implements IOperator {
    private ISubject publisher = new PassthroughSubject();

    //publisher
    public ICancellable subscribe(ISubscriber subscriber) {
        return publisher.subscribe(subscriber);
    }

    //subscriber
    protected void onValue(Object value) {
        Object newValue = processValue(value);
        publisher.sendValue(newValue);
    }

    protected void onCompletion(Completion completion) {
        Completion newCompletion = processCompletion(completion);
        publisher.sendCompletion(newCompletion);
    }


    public Object processValue(Object value) {
        return value;
    }

    public Completion processCompletion(Completion completion) {
        return completion;
    }
}
