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
import ru.asolovyov.combime.api.ISubscription;

/**
 *
 * @author Администратор
 */
public abstract class Operator extends Subscriber implements IOperator {
    protected ISubject publisher = new PassthroughSubject();
    
    public IPublisher to(IOperator operator) {
        System.out.println("OP TO " + operator);
        publisher.subscribe(operator);
        return operator;
    }

    public ICancellable subscribe(ISubscriber subscriber) {
        ISubscription cancellable = (ISubscription)publisher.subscribe(subscriber);
        publisher.subscriptionDidRequestValues(cancellable, Demand.UNLIMITED);
        return cancellable;
    }

    //subscriber
    protected void onValue(Object value) {
        Object newValue = mapValue(value);
        System.out.println("OP ON_VALUE: " + publisher);
        publisher.sendValue(newValue);
    }

    protected void onCompletion(Completion completion) {
        Completion newCompletion = mapCompletion(completion);
        publisher.sendCompletion(newCompletion);
    }

    protected Object mapValue(Object value) {
        return value;
    }

    protected Completion mapCompletion(Completion completion) {
        return completion;
    }

    public void subscriptionDidRequestValues(ISubscription subscription, Demand demand) {
        publisher.subscriptionDidRequestValues(subscription, demand);
    }

    public void subscriptionDidCancel(ISubscription subscription) {
        publisher.subscriptionDidCancel(subscription);
    }
}
