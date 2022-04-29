/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.publishers;

import ru.asolovyov.combime.api.ISubscription;
import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.common.Demand;
import ru.asolovyov.combime.common.Subscription;

/**
 *
 * @author Администратор
 */
public class Fail extends Publisher {
    private Completion completion;

    public Fail(Exception failure) {
        super();
        completion = new Completion(failure);
    }

    public Fail() {
        super();
        completion = new Completion(false);
    }

    public void subscriptionDidRequestValues(ISubscription subscription, Demand demand) {
        ((Subscription)subscription).sendCompletion(completion);
    }
}
