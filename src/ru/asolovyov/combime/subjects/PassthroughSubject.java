/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.asolovyov.combime.subjects;

import ru.asolovyov.combime.publishers.Publisher;
import java.util.Enumeration;
import ru.asolovyov.combime.api.ISubject;
import ru.asolovyov.combime.api.ISubscription;
import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.common.Demand;
import ru.asolovyov.combime.common.S;
import ru.asolovyov.combime.common.Subscription;

/**
 *
 * @author Администратор
 */
public class PassthroughSubject extends Publisher implements ISubject {
    public void sendValue(Object value) {
        Object[] susbcriptions = S.toArray(subscriptions);
        S.debug(this + " PTS sendValue " + value);
        for (int i = 0; i < susbcriptions.length; i++) {
            Subscription subscription = (Subscription) susbcriptions[i];
            S.debug("to " + subscription.getSubscriber());
            subscription.sendValue(value);
        }
    }

    public void sendCompletion(Completion completion) {
        Object[] subscriptions = S.toArray(this.subscriptions);
        S.debug(this + " PTS Sending completion to " + subscriptions.length + " subscriptions");
        for (int i = 0; i < subscriptions.length; i++) {
            Subscription subscription = (Subscription) subscriptions[i];
            S.debug("to " + subscription.getSubscriber());
            subscription.sendCompletion(completion);
        }
        S.debug("DONE " + subscriptions.length);
        this.subscriptions.removeAllElements();
    }

    public void subscriptionDidRequestValues(ISubscription subscription, Demand demand) {
        super.subscriptionDidRequestValues(subscription, demand);
        S.debug(this.getId() + " PTS subscriptionDidRequestValues(ISubscription subscription, Demand demand)");
    }
}
