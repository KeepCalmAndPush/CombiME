/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.api;

import ru.asolovyov.combime.impl.Completion;
import ru.asolovyov.combime.impl.Demand;

/**
 *
 * @author Администратор
 */
public interface ISubscriber {
    public Class getInputType();
    public Class getFailureType();

    public void receiveSubscription(ISubscription subscription);

    /** MUST be of getInputType() type*/
    public Demand receiveInput(Object input);

    /** Completion's failure MUST be of getFailureType() type*/
    public void receiveCompletion(Completion completion);
}
