/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.operators.combining;

import java.util.Enumeration;
import java.util.Hashtable;
import ru.asolovyov.combime.api.ICancellable;
import ru.asolovyov.combime.api.IPublisher;
import ru.asolovyov.combime.api.ISubscription;
import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.common.Demand;
import ru.asolovyov.combime.common.Sink;
import ru.asolovyov.combime.common.Subscription;
import ru.asolovyov.combime.operators.Operator;

/**
 *
 * @author Администратор
 */
public class SwitchToLatest extends Operator {
    private ICancellable latestSubscription;

    public Demand receiveInput(Object input) {
        if (latestSubscription != null) {
            latestSubscription.cancel();
        }
        
        IPublisher publisher = (IPublisher) input;
        latestSubscription = (ISubscription) publisher.sink(new Sink() {
            protected void onValue(Object value) {
                SwitchToLatest.this.sendValue(value);
            }
            protected void onCompletion(Completion completion) {
                SwitchToLatest.this.sendCompletion(completion);
            }
        });
        
        return Demand.UNLIMITED;
    }
}