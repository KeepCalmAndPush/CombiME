/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.operators;

import ru.asolovyov.combime.api.ICancellable;
import ru.asolovyov.combime.api.IOperator;
import ru.asolovyov.combime.api.ISubscriber;
import ru.asolovyov.combime.api.ISubscription;
import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.common.Demand;
import ru.asolovyov.combime.subjects.PassthroughSubject;
import ru.asolovyov.combime.common.S;

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
        S.debug("Operator " + this + " will receive input " + input);
        sendValue(newValue);
        return Demand.UNLIMITED;
    }

    public void receiveCompletion(Completion completion) {
        Completion newCompletion = mapCompletion(completion);
        sendCompletion(newCompletion);
    }

    public ICancellable sink(ISubscriber subscriber) {
        S.debug("Operator will receive subscription " + subscriber);
        ISubscription subs = createSubscription(subscriber);
        subscriptions.addElement(subs);
        subscriber.receiveSubscription(subs);
        S.debug("Subscriptions count: " + subscriptions.size());
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
