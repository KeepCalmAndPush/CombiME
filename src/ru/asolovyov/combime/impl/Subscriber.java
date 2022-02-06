/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.impl;

import ru.asolovyov.combime.utils.Utils;
import ru.asolovyov.combime.api.ISubscriber;
import ru.asolovyov.combime.api.ISubscription;

/**
 *
 * @author Администратор
 */
public abstract class Subscriber implements ISubscriber {
    protected abstract Demand processInput(Object input);
    protected abstract void processCompletion(Completion completion);

    private Class inputType;
    private Class failureType;

    Subscriber(Class inputType, Class failureType) {
        super();
        this.inputType = inputType;
        this.failureType = failureType;
    }
    
    public Class getInputType() {
        return inputType;
    }

    public Class getFailureType() {
        return failureType;
    }

    public void receiveSubscription(ISubscription subscription) {
        subscription.requestValues(Demand.UNLIMITED);
    }

    public Demand receiveInput(Object input) {
        Utils.assertIsA(
                "Subscriber's input",
                getInputType(),
                " received input",
                input.getClass()
                );
        return processInput(input);
    }

    public void receiveCompletion(Completion completion) {
        Utils.assertIsA(
                "Subscriber's failure",
                getFailureType(),
                "completion's failure",
                completion.getFailure().getClass()
                );
        processCompletion(completion);
    }
}
