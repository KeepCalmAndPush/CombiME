/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.asolovyov.combime.impl;

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
        subscriber = null;
    }

    protected boolean hasNextValue = false;
    private boolean isCompleted = false;

    public void requestValues(Demand demand) {
        this.demand = demand;
        System.out.println("Subscription " + this.getId() + " requested values");
        this.delegate.subscriptionDidRequestValues(this, demand);
    }

    public void sendValue(Object value) {
//        hasNextValue = true;

        if (!mayEmitValue()) {
            return;
        }

        getDemand().decrement();
        Demand next = getSubscriber().receiveInput(value);
        getDemand().add(next);

//        hasNextValue = false;

//        if (mayComplete()) {
//            Completion completion = emitCompletion();
//            getSubscriber().receiveCompletion(completion);
//            isCompleted = true;
//        }
    }

    public void sendCompletion(Completion completion) {
        getSubscriber().receiveCompletion(completion);
    }

    public ISubscriber getSubscriber() {
        return subscriber;
    }

    protected Demand getDemand() {
        return demand;
    }

    protected boolean mayEmitValue() {
//        if (!hasNextValue || isCompleted) {
//            return false;
//        }
        if (subscriber == null) {
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

//    protected boolean mayComplete() {
//        if (subscriber == null || isCompleted) {
//            return false;
//        }
//        return demand.getValue() == 0;
//    }
//
//    protected Completion emitCompletion() {
//        return new Completion(true, null);
//    }
}
