/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.api;

import ru.asolovyov.combime.impl.Demand;

/**
 *
 * @author Администратор
 */
public interface ISubscriptionDelegate {
    void subscriptionDidRequestValues(ISubscription subscription, Demand demand);
    void subscriptionDidCancel(ISubscription subscription);
}
