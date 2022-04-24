/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.operators;

import java.util.Hashtable;
import ru.asolovyov.combime.api.ICancellable;
import ru.asolovyov.combime.api.IPublisher;
import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.common.Demand;
import ru.asolovyov.combime.common.Sink;

/**
 *
 * @author Администратор
 */
public class Merge extends Operator {
    private Hashtable cancellables = new Hashtable();

    public Merge(IPublisher publisher) {
        this(new IPublisher[]{publisher});
    }

    public Merge(IPublisher[] publishers) {
        for (int i = 0; i < publishers.length; i++) {
            final int index = i;
            IPublisher publisher = publishers[i];
            final ICancellable token = publisher.sink(new Sink() {
                protected void onValue(Object value) {
                    Merge.this.processInput(value);
                }

                protected void onCompletion(Completion completion) {
                    Merge.this.processCompletion(completion, new Integer(index));
                }
            });
            cancellables.put(new Integer(i), token);
        }
    }

    public Demand receiveInput(Object input) {
        Object newValue = mapValue(input);
        processInput(newValue);
        return Demand.UNLIMITED;
    }

    public void receiveCompletion(Completion completion) {
        processCompletion(completion, null);
    }

    private void processInput(Object input) {
        sendValue(input);
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
        
        sendCompletion(new Completion(true));
    }
}
