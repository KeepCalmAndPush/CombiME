/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime;

/**
 *
 * @author Администратор
 */
public abstract class Publisher implements IPublisher {
    protected abstract Cancellable attachSubscriber(ISubscriber subscriber);

    private Class outputType;
    private Class failureType;
    
    private ISubscriber subscriber;

    Publisher(Class outputType, Class failureType) {
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

    public Cancellable receiveSubscriber(ISubscriber subscriber) {
        Utils.assertIsA("Publisher's output", getOutputType(), "subscriber's input", subscriber.getInputType());
        Utils.assertIsA("Publisher's failure", getFailureType(), "subscriber's failure", subscriber.getFailureType());

        this.subscriber = subscriber;

        return attachSubscriber(this.subscriber);
    }
}
