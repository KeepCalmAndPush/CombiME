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
public class Subscriber implements ISubscriber {
    protected ISubscription subscription;
    protected void onValue(Object value) { };
    protected void onCompletion(Completion completion) {};

    public void receiveSubscription(ISubscription subscription) {
        this.subscription = subscription;
    }

    public Demand receiveInput(Object input) {
        onValue(input);
        return Demand.UNLIMITED;
    }

    public void receiveCompletion(Completion completion) {
        onCompletion(completion);
    }
}
