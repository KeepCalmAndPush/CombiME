/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime;

/**
 *
 * @author Администратор
 */
public interface IPublisher {
    public Class getOutputType();
    public Class getFailureType();

    /** ISubscriber's InputType and FailureType MUST be equal to OutputType and FailureType */
    public Cancellable receiveSubscriber(ISubscriber subscriber);
}
