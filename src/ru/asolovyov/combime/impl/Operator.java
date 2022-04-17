/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.impl;

import ru.asolovyov.combime.api.ICancellable;
import ru.asolovyov.combime.api.IOperator;
import ru.asolovyov.combime.api.ISubscriber;
import ru.asolovyov.combime.api.ISubscription;

/**
 *
 * @author Администратор
 */
public abstract class Operator extends PassthroughSubject implements IOperator {
    protected ISubscription subscription;

    public void receiveSubscription(ISubscription subscription) {
        this.subscription = subscription;
    }

    public Demand receiveInput(Object input) {
        Object newValue = mapValue(input);
        sendValue(newValue);
        return Demand.UNLIMITED;
    }

    public void receiveCompletion(Completion completion) {
        Completion newCompletion = mapCompletion(completion);
        sendCompletion(newCompletion);
    }

    public ICancellable sink(ISubscriber subscriber) {
        ISubscription subs = createSubscription(subscriber);
        subscriber.receiveSubscription(subs);
        subscriptions.addElement(subs);
        return subs;
    }

    protected Object mapValue(Object value) {
        return value;
    }

    protected Completion mapCompletion(Completion completion) {
        return completion;
    }

    public void subscriptionDidRequestValues(ISubscription subscription, Demand demand) {
        if (this.subscription != null) {
            this.subscription.requestValues(demand);
        }
    }
}