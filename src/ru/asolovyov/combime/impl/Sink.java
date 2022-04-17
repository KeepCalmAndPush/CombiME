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
        System.out.println("SINK WILL REQUEST VALUE FROM " + subscription);
        subscription.requestValues(Demand.UNLIMITED);
    }

    public Demand receiveInput(Object input) {
        System.out.println("SINK DID RECEIVE input " + input);
        onValue(input);
        return Demand.UNLIMITED;
    }

    public void receiveCompletion(Completion completion) {
        System.out.println("SINK DID RECEIVE completion " + completion);
        onCompletion(completion);
    }
}
