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
        Enumeration elements = subscriptions.elements();
        S.debug(this.getId() + " PTS sendValue " + value);
        while (elements.hasMoreElements()) {
            Subscription element = (Subscription) elements.nextElement();
            S.debug("to " + element.getSubscriber());
            element.sendValue(value);
        }
    }

    public void sendCompletion(Completion completion) {
        Enumeration elements = subscriptions.elements();
        S.debug(this.getId() + " PTS Sending completion");
        while (elements.hasMoreElements()) {
            Subscription element = (Subscription) elements.nextElement();
            S.debug(this.getId() + " PTS Sending completion to " + element);
            element.sendCompletion(completion);
        }
        subscriptions.removeAllElements();
    }

    public void subscriptionDidRequestValues(ISubscription subscription, Demand demand) {
        super.subscriptionDidRequestValues(subscription, demand);
        S.debug(this.getId() + " PTS subscriptionDidRequestValues(ISubscription subscription, Demand demand)");
    }
}
