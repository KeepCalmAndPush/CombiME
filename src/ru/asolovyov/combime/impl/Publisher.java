/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.impl;

import ru.asolovyov.combime.utils.Utils;
import ru.asolovyov.combime.api.ISubscriber;
import ru.asolovyov.combime.api.ICancellable;
import ru.asolovyov.combime.api.IPublisher;
import ru.asolovyov.combime.api.ISubscription;

/**
 *
 * @author Администратор
 */
public abstract class Publisher implements IPublisher {
    private Class outputType;
    private Class failureType;

    public Publisher(Class outputType, Class failureType) {
        super();
        this.outputType = outputType;
        this.failureType = failureType;
    }

    public Class getOutputType() {
        return outputType;
    }

    public Class getFailureType() {
        return failureType;
    }

    protected abstract ISubscription createSubscription(ISubscriber subscriber);

    public ICancellable receiveSubscriber(ISubscriber subscriber) {
        Utils.assertIsA("Publisher's output", getOutputType(), "subscriber's input", subscriber.getInputType());
        Utils.assertIsA("Publisher's failure", getFailureType(), "subscriber's failure", subscriber.getFailureType());

        ISubscription subscription = createSubscription(subscriber);
        subscriber.receiveSubscription(subscription);

        return subscription;
    }
}
