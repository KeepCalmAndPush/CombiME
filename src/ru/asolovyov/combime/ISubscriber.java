/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime;

/**
 *
 * @author Администратор
 */
public interface ISubscriber {
    public Class getInputType();
    public Class getFailureType();

    /** MUST be of getInputType() type*/
    public Demand receiveInput(Object input);

    public void receiveSubscription(ISubscription subscription);

    /** Completion's failure MUST be of getFailureType() type*/
    public void receiveCompletion(Completion completion);
}
