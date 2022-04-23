/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.impl;

import java.util.Hashtable;
import ru.asolovyov.combime.api.ICancellable;
import ru.asolovyov.combime.api.IPublisher;

/**
 *
 * @author Администратор
 */
public abstract class Reduce extends Operator {
    private Object result;
    private Hashtable cancellables = new Hashtable();

    public Reduce(Object initialResult, IPublisher publisher) {
        this(initialResult, new IPublisher[]{publisher});
    }

    public Reduce(Object initialResult, IPublisher[] publishers) {
        this.result = initialResult;
        for (int i = 0; i < publishers.length; i++) {
            final int index = i;
            IPublisher publisher = publishers[i];
            final ICancellable token = publisher.sink(new Sink() {
                protected void onValue(Object value) {
                    Reduce.this.processInput(value);
                }

                protected void onCompletion(Completion completion) {
                    Reduce.this.processCompletion(completion, new Integer(index));
                }
            });
            cancellables.put(new Integer(i), token);
        }
    }

    protected abstract Object reduce(Object subresult, Object currentValue);

    public Demand receiveInput(Object input) {
        Demand demand = super.receiveInput(input);
        processInput(input);
        return demand;
    }

    public void receiveCompletion(Completion completion) {
        processCompletion(completion, null);
    }

    private void processInput(Object input) {
        result = reduce(result, input);
    }

    private void processCompletion(Completion completion, Integer nullableCancellableKey) {
        if (nullableCancellableKey != null) {
            cancellables.remove(nullableCancellableKey);
        }

        if (!completion.isSuccess()) {
            cancellables.clear();
            sendCompletion(completion);
            return;
        }

        if (!cancellables.isEmpty()) {
            return;
        }

        sendValue(result);
        sendCompletion(new Completion(true));
    }
}
