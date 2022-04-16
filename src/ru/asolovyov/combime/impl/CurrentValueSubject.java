/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.impl;

import ru.asolovyov.combime.api.ICancellable;
import ru.asolovyov.combime.api.ISubscriber;
import ru.asolovyov.combime.api.ISubscription;

/**
 *
 * @author Администратор
 */
public class CurrentValueSubject extends PassthroughSubject {
    private Object value;
    private Exception failure;

    public CurrentValueSubject(Object currentValue) {
        this.value = currentValue;
    }

    public ICancellable subscribe(ISubscriber subscriber) {
        ICancellable result = super.subscribe(subscriber);
        if (value != null) {
            super.sendValue(value);
        } else if (failure != null) {
            super.sendCompletion(new Completion(false, failure));
        }
        return result;
    }
    
    public void sendValue(Object value) {
        this.value = value;
        System.out.println(this.getClass().getName() + " CVS sendValue " + value);
        super.sendValue(value);
    }

    public void sendCompletion(Completion completion) {
        failure = completion.getFailure();
        super.sendCompletion(completion);
    }

    public void subscriptionDidRequestValues(ISubscription subscription, Demand demand) {
        System.out.println(this.getClass().getName() + " CVS subscriptionDidRequestValues " + subscription + " " + demand.getValue());
        if (value != null) {
            System.out.println(this.getClass().getName() + " CVS subscriptionDidRequest Value " + value);
            subscription.getSubscriber().receiveInput(value);
            subscription.getSubscriber().receiveCompletion(new Completion(true, null));
        } else if (failure != null) {
            System.out.println(this.getClass().getName() + " CVS subscriptionDidRequest Failure " + failure);
            subscription.getSubscriber().receiveCompletion(new Completion(false, failure));
        }
        System.out.println(this.getClass().getName() + " CVS subscriptionDidRequest NOPE");
    }
}