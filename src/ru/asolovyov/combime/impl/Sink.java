/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.impl;

import ru.asolovyov.combime.api.ISubscription;

/**
 *
 * @author Администратор
 */
public abstract class Sink extends Subscriber {
    protected void onValue(Object value) { };
    protected void onCompletion(Completion completion) {};

    public void receiveSubscription(ISubscription subscription) {
        super.receiveSubscription(subscription);
        this.subscription.requestValues(Demand.UNLIMITED);
    }

    public Demand receiveInput(Object input) {
        onValue(input);
        return Demand.UNLIMITED;
    }

    public void receiveCompletion(Completion completion) {
        onCompletion(completion);
    }
}
