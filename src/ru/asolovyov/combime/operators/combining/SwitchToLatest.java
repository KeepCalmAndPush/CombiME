/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.asolovyov.combime.operators.combining;

import ru.asolovyov.combime.api.ICancellable;
import ru.asolovyov.combime.api.IPublisher;
import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.common.Demand;
import ru.asolovyov.combime.common.S;
import ru.asolovyov.combime.common.Sink;
import ru.asolovyov.combime.operators.Operator;
import ru.asolovyov.combime.subjects.CurrentValueSubject;

/**
 *
 * @author Администратор
 */
public class SwitchToLatest extends Operator {

    private ICancellable latestSubscription;

    public Demand receiveInput(Object input) {
        S.debug(this.getId() + " SWTL(Object input) " + input);

        if (latestSubscription != null) {
            latestSubscription.cancel();
        }

        IPublisher publisher = (IPublisher) input;
        latestSubscription = publisher.sink(new Sink() {
            protected void onValue(Object value) {
                S.debug(getId() + " SWTL WILL SEND VALUE " + value);
                SwitchToLatest.this.sendValue(value);
            }
            protected void onCompletion(Completion completion) {
                S.debug(getId() + " SWTL WILL SEND COMPLETION " + completion);
                SwitchToLatest.this.sendCompletion(completion);
            }
        });

        return Demand.UNLIMITED;
    }
}
