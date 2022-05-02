/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.asolovyov.combime.common;

import ru.asolovyov.combime.api.ISubscriber;
import ru.asolovyov.combime.api.ISubscription;
import ru.asolovyov.combime.api.ISubscriptionDelegate;

/**
 *
 * @author Администратор
 */
public class Subscription implements ISubscription {
    private long id;
    private static long ID_COUNTER = 0;
    private ISubscriber subscriber;
    private Demand demand;
    private ISubscriptionDelegate delegate;

    private boolean isCompleted = false;
    private boolean isCancelled = false;

    { generateId(); }

    public Subscription(ISubscriber subscriber) {
        this.subscriber = subscriber;
    }
    
    private synchronized void generateId() {
        id = Subscription.ID_COUNTER++;
    }

    public long getId() {
        return id;
    }

    public void cancel() {
        this.delegate.subscriptionDidCancel(this);
        isCancelled = true;
    }

    public void requestValues(Demand demand) {
        this.demand = demand;
        S.debug("Subscription " + this.getId() + " requested values");
        this.delegate.subscriptionDidRequestValues(this, demand);
    }

    public void sendValue(Object value) {
        if (!mayEmitValue()) {
            return;
        }

        getDemand().decrement();
        Demand next = getSubscriber().receiveInput(value);
        getDemand().add(next);

        if (demand.getValue() > 0 && demand != Demand.UNLIMITED) {
            delegate.subscriptionDidRequestValues(this, getDemand());
        }
    }

    public void sendCompletion(Completion completion) {
        if (isCompleted) {
            return;
        }
        
        isCompleted = true;
        getSubscriber().receiveCompletion(completion);
        delegate.subscriptionDidCancel(this);
    }

    public ISubscriber getSubscriber() {
        return subscriber;
    }

    protected Demand getDemand() {
        return demand;
    }

    protected boolean mayEmitValue() {
        if (isCompleted || isCancelled) {
            return false;
        }
        if (getDemand() == null) {
            return false;
        }

        boolean mayEmitValue = demand == Demand.UNLIMITED;
        mayEmitValue |= getDemand().getValue() > 0;
        return mayEmitValue;
    }
    
    public ISubscriptionDelegate getDelegate() {
        return delegate;
    }

    public void setDelegate(ISubscriptionDelegate delegate) {
        this.delegate = delegate;
    }
}
