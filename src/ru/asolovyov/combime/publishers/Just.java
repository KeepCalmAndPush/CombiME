/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.asolovyov.combime.publishers;

import ru.asolovyov.combime.api.ISubscription;
import ru.asolovyov.combime.common.Demand;

/**
 *
 * @author Администратор
 */
public class Just extends Publisher {

    private Object value;

    public Just(Object value) {
        this.value = value;
    }

    public void subscriptionDidRequestValues(ISubscription subscription, Demand demand) {
        super.subscriptionDidRequestValues(subscription, demand);
        subscription.getSubscriber().receiveInput(value);
    }
}
