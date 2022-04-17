/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.impl;

import ru.asolovyov.combime.api.ICancellable;
import ru.asolovyov.combime.api.IOperator;
import ru.asolovyov.combime.api.IPublisher;
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

//    public ICancellable sink(ISubscriber subscriber) {
//        ICancellable result = super.sink(subscriber);
//        return result;
//    }
//
//    public IPublisher to(IOperator operator) {
//        IPublisher result = super.to(operator);
//        return result;
//    }
    
    public void sendValue(Object value) {
        this.value = value;
        System.out.println(this.getId() + " CVS sendValue " + value);
        super.sendValue(value);
    }

    public void sendCompletion(Completion completion) {
        failure = completion.getFailure();
        super.sendCompletion(completion);
    }

    public void subscriptionDidRequestValues(ISubscription subscription, Demand demand) {
        System.out.println(this.getId() + " CVS subscriptionDidRequestValues " + subscription.getSubscriber());
        
        if (value != null) {
            System.out.println(this.getId() + " CVS subscription will receive value " + value);
            this.sendValue(value);
        } else if (failure != null) {
            System.out.println(this.getId() + " CVS subscription will receive failure " + failure);
            this.sendCompletion(new Completion(false, failure));
        } else {
            System.out.println(this.getId() + " CVS subscriptionDidRequest NOPE");
        }
    }
}