/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.utils;

import ru.asolovyov.combime.api.ISubscription;
import ru.asolovyov.combime.impl.Completion;
import ru.asolovyov.combime.impl.Demand;
import ru.asolovyov.combime.impl.Publisher;
import ru.asolovyov.combime.impl.Subscription;

/**
 *
 * @author Администратор
 */
public class Empty extends Publisher {
    public void subscriptionDidRequestValues(ISubscription subscription, Demand demand) {
        ((Subscription)subscription).sendCompletion(new Completion(true));
    }
}
