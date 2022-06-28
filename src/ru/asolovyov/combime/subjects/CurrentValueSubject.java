/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.asolovyov.combime.subjects;

import ru.asolovyov.combime.api.ISubscription;
import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.common.Demand;
import ru.asolovyov.combime.common.S;

/**
 *
 * @author Администратор
 */
public class CurrentValueSubject extends PassthroughSubject {

    private Object value;
    private Completion completion;

    public CurrentValueSubject(Object currentValue) {
        this.value = currentValue;
    }

    public void sendValue(Object value) {
        this.value = value;
        S.debug(this.getId() + " CVS sendValue " + value);
        super.sendValue(value);
    }

    public void sendCompletion(Completion completion) {
        this.completion = completion;
        super.sendCompletion(completion);
    }

    public void subscriptionDidRequestValues(ISubscription subscription, Demand demand) {
        super.subscriptionDidRequestValues(subscription, demand);

        S.debug(this.getId() + " CVS subscriptionDidRequestValues " + subscription.getSubscriber());

        if (getValue() != null) {
            S.debug(this.getId() + " CVS subscription will receive value " + getValue());
            this.sendValue(getValue());
        } else if (completion != null) {
            S.debug(this.getId() + " CVS subscription will receive completion " + completion);
            this.sendCompletion(completion);
        } else {
            S.debug(this.getId() + " CVS subscriptionDidRequest NOPE");
        }
    }

    /**
     * @return the value
     */
    public Object getValue() {
        return value;
    }
}
