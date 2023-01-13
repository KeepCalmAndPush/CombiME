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
        S.debug(this.getId() + " KEK(Object input) " + input);

        if (latestSubscription != null) {
            latestSubscription.cancel();
        }

        IPublisher publisher = (IPublisher) input;
        latestSubscription = publisher.sink(new Sink() {
            protected void onValue(Object value) {
                SwitchToLatest.this.sendValue(value);
            }
            protected void onCompletion(Completion completion) {
                SwitchToLatest.this.sendCompletion(completion);
            }
        });

//        if (publisher instanceof CurrentValueSubject) {
//            CurrentValueSubject cvs = ((CurrentValueSubject)publisher);
//            S.debug(this.getId() + " KEK CVS FLUSH! " + cvs.getValue());
//            cvs.sendValue(cvs.getValue());
//        }

        return Demand.UNLIMITED;
    }
}
