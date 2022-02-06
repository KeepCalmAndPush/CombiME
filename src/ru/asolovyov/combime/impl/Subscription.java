/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.impl;

import ru.asolovyov.combime.api.ISubscriber;
import ru.asolovyov.combime.api.ISubscription;

/**
 *
 * @author Администратор
 */
public abstract class Subscription implements ISubscription {
    private long id;
    private static long ID_COUNTER = 0;
    private ISubscriber subscriber;
    private Demand demand;

    private Class inputType;
    private Class failureType;

    Subscription(Class inputType, Class failureType, ISubscriber subscriber) {
        this.inputType = inputType;
        this.failureType = failureType;
        this.subscriber = subscriber;
    }

    { generateId(); }
    
    private synchronized void generateId() {
        id = Subscription.ID_COUNTER++;
    }

    public long getId() {
        return id;
    }

    public void cancel() {
        subscriber = null;
    }

    protected abstract Object emitValue();
    protected abstract Completion emitCompletion();

    public void requestValues(Demand demand) {
        this.demand = demand;

        while (mayEmitValue()) {
            Object object = emitValue();
            Demand next = getSubscriber().receiveInput(object);
            getDemand().add(next);
        }

        Completion completion = emitCompletion();
        getSubscriber().receiveCompletion(completion);
    }

    protected ISubscriber getSubscriber() {
        return subscriber;
    }

    protected Demand getDemand() {
        return demand;
    }

    protected boolean mayEmitValue() {
        boolean mayEmitValue = demand == Demand.UNLIMITED;
        mayEmitValue |= getDemand().getValue() > 0;
        return mayEmitValue;
    }
}
