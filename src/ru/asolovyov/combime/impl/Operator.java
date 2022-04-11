/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.impl;

import ru.asolovyov.combime.api.ICancellable;
import ru.asolovyov.combime.api.IOperator;
import ru.asolovyov.combime.api.IPublisher;
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
        return getPublisher().subscribe(subscriber);
    }

    public IPublisher to(IOperator operator) {
        subscribe(operator);
        return operator;
    }

    //subscriber
    protected void onValue(Object value) {
        Object newValue = mapValue(value);
        System.out.println("VALUE " + value + " MAPPED TO " + newValue);
        getPublisher().sendValue(newValue);
    }

    protected void onCompletion(Completion completion) {
        Completion newCompletion = mapCompletion(completion);
        getPublisher().sendCompletion(newCompletion);
    }

    public Object mapValue(Object value) {
        return value;
    }

    public Completion mapCompletion(Completion completion) {
        return completion;
    }

    protected ISubject getPublisher() {
        return publisher;
    }

    protected void setPublisher(ISubject publisher) {
        this.publisher = publisher;
    }
}
