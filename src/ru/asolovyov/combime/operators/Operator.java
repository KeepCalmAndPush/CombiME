/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.asolovyov.combime.operators;

import ru.asolovyov.combime.api.IOperator;
import ru.asolovyov.combime.api.ISubscriber;
import ru.asolovyov.combime.api.ISubscription;
import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.common.Demand;
import ru.asolovyov.combime.common.S;
import ru.asolovyov.combime.subjects.PassthroughSubject;

/**
 *
 * @author Администратор
 */
public abstract class Operator extends PassthroughSubject implements IOperator {

    protected boolean isTry = false;
    protected ISubscription subscription;
    protected Demand requestsLeft = Demand.NONE;

    public void receiveSubscription(ISubscription subscription) {
        this.subscription = subscription;
    }

    public Demand receiveInput(Object input) {
        if (!this.isTry) {
            return _receiveInput(input);
        }

        try {
            return _receiveInput(input);
        } catch (Exception e) {
            sendCompletion(new Completion(e));
            subscription.cancel();
            return Demand.NONE;
        }
    }

    public void receiveCompletion(Completion completion) {
        _receiveCompletion(completion);
    }

    protected Demand _receiveInput(Object input) {
        Object newValue = mapValue(input);
        S.debug("Operator " + this + " will receive input " + input);
        sendValue(newValue);

        Demand demand = this.requestsLeft.copy();
        this.requestsLeft.decrement();

        return demand;
    }

    protected void _receiveCompletion(Completion completion) {
        if (completion.isSuccess()) {
            sendCompletion(completion);
            return;
        }

        Completion newCompletion = new Completion(
                completion.isSuccess(),
                this.mapError(completion.getFailure()));

        sendCompletion(newCompletion);
    }

    public ISubscription sink(ISubscriber subscriber) {
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

    protected Exception mapError(Exception error) {
        return error;
    }

    public void subscriptionDidRequestValues(ISubscription subscription, Demand demand) {
        if (this.subscription != null) {
            super.subscriptionDidRequestValues(subscription, demand);
            this.subscription.requestValues(demand);

            if (this.requestsLeft.getValue() < demand.getValue()) {
                this.requestsLeft = demand.copy();
            }
        }
    }
}
