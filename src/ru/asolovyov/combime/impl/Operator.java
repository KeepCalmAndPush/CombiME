/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.impl;

import ru.asolovyov.combime.api.ICancellable;
import ru.asolovyov.combime.api.IOperator;
import ru.asolovyov.combime.api.IPublisher;
import ru.asolovyov.combime.api.ISubject;
import ru.asolovyov.combime.api.ISubscriber;

/**
 *
 * @author Администратор
 */
public abstract class Operator extends Subscriber implements IOperator {
    protected ISubject publisher = new CurrentValueSubject(null);
    
    public IPublisher to(IOperator operator) {
        System.out.println(this.getClass().getName() + " OP TO " + operator);
        publisher.subscribe(operator);
        return operator;
    }

    public ICancellable subscribe(ISubscriber subscriber) {
        System.out.println(this.getClass().getName() + " OP SUBSCRIBE " + subscriber);
        return publisher.subscribe(subscriber);
    }

    //subscriber
    protected void onValue(Object value) {
        Object newValue = mapValue(value);
        System.out.println(this.getClass().getName() + " OP ON_VALUE: " + publisher);
        publisher.sendValue(newValue);
    }

    protected void onCompletion(Completion completion) {
        Completion newCompletion = mapCompletion(completion);
        publisher.sendCompletion(newCompletion);
    }

    protected Object mapValue(Object value) {
        System.out.println("OP MAP VALUE " + value);
        return value;
    }

    protected Completion mapCompletion(Completion completion) {
        return completion;
    }
}
