/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.asolovyov.combime.api;

import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.common.Demand;
import ru.asolovyov.threading.Scheduler;

/**
 *
 * @author Администратор
 */
public interface ISubscriptionDelegate {

    public void subscriptionDidRequestValues(ISubscription subscription, Demand demand);

    public void subscriptionDidCancel(ISubscription subscription);

    public void subscriptionDidSendValue(ISubscription subscription, Object value);

    public void subscriptionDidSendCompletion(ISubscription subscription, Completion compleation);

    public Scheduler getReceptionScheduler();
}
