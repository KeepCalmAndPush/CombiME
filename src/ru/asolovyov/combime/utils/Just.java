/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.utils;

import ru.asolovyov.combime.api.ICancellable;
import ru.asolovyov.combime.api.IOperator;
import ru.asolovyov.combime.api.IPublisher;
import ru.asolovyov.combime.api.ISubscriber;
import ru.asolovyov.combime.api.ISubscription;
import ru.asolovyov.combime.impl.CurrentValueSubject;
import ru.asolovyov.combime.impl.Demand;
import ru.asolovyov.combime.impl.Publisher;

/**
 *
 * @author Администратор
 */
public class Just extends Publisher {
    private CurrentValueSubject subject = new CurrentValueSubject(null);

    public Just(Object value) {
        subject.sendValue(value);
    }

    public ICancellable sink(ISubscriber subscriber) {
        return subject.sink(subscriber);
    }

    public IPublisher to(IOperator operator) {
        return subject.to(operator);
    }

    public void subscriptionDidRequestValues(ISubscription subscription, Demand demand) {
        subject.subscriptionDidRequestValues(subscription, demand);
    }

    public void subscriptionDidCancel(ISubscription subscription) {
        subject.subscriptionDidCancel(subscription);
    }
}