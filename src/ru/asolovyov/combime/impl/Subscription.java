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

    protected abstract Object emitValue();
    protected abstract Completion emitCompletion();

    public void requestValues(Demand demand) {
        this.demand = demand;
        passInputToSubscriber();
    }

    protected void passInputToSubscriber() {
        if (mayEmitValue()) {
            getDemand().decrement();
            Object object = emitValue();
            Demand next = getSubscriber().receiveInput(object);
            getDemand().add(next);
            hasNextValue = false;
        } else {
            return;
        }

        if (mayComplete()) {
            Completion completion = emitCompletion();
            getSubscriber().receiveCompletion(completion);
            isCompleted = true;
        }
    }

    protected ISubscriber getSubscriber() {
        return subscriber;
    }

    protected Demand getDemand() {
        return demand;
    }

    protected boolean mayEmitValue() {
        if (!hasNextValue || isCompleted) {
            return false;
        }
        if (subscriber == null) {
            return false;
        }
        boolean mayEmitValue = demand == Demand.UNLIMITED;
        mayEmitValue |= getDemand().getValue() > 0;
        return mayEmitValue;
    }

    protected boolean mayComplete() {
        if (subscriber == null || isCompleted) {
            return false;
        }
        return demand.getValue() == 0;
    }
}
