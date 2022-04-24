/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.api;

import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.common.Demand;

/**
 *
 * @author Администратор
 */
public interface ISubscriber {
    public void receiveSubscription(ISubscription subscription);
    public Demand receiveInput(Object input);
    public void receiveCompletion(Completion completion);
}
