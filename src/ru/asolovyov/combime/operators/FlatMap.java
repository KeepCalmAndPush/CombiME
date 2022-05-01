/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.operators;

import ru.asolovyov.combime.api.ICancellable;
import ru.asolovyov.combime.api.IOperator;
import ru.asolovyov.combime.api.IPublisher;
import ru.asolovyov.combime.api.ISubscriber;
import ru.asolovyov.combime.api.ISubscription;
import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.common.Demand;
import ru.asolovyov.combime.common.Sink;

/**
 *
 * @author Администратор
 */
public abstract class FlatMap extends Operator {
    protected abstract IPublisher flatMap(Object value);

    public Demand receiveInput(Object input) {
        IPublisher publisher = flatMap(input);
        publisher.sink(new Sink() {
            protected void onCompletion(Completion completion) {
                FlatMap.this.sendCompletion(completion);
            }

            protected void onValue(Object value) {
                FlatMap.this.sendValue(value);
            }
        });
        return Demand.UNLIMITED;
    }
}
