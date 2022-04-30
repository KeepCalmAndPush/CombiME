/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.publishers;

import java.util.Hashtable;
import ru.asolovyov.combime.api.ISubscription;
import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.common.Demand;
import ru.asolovyov.combime.common.Subscription;

/**
 *
 * @author Администратор
 */
public class Sequence extends Publisher {
    private Object[] sequence;
    private Hashtable servings = new Hashtable();
    
    public Sequence(Object[] sequence) {
        super();
        this.sequence = sequence;
    }

    public void subscriptionDidRequestValues(ISubscription subscription, Demand demand) {
        long count = demand.getValue();
        Subscription sub = ((Subscription)subscription);
        if (demand == Demand.UNLIMITED || demand.getValue() >= sequence.length) {
            for(int i = 0; i < sequence.length; i++) {
                sub.sendValue(sequence[i]);
            }
            sub.sendCompletion(new Completion(true));
            return;
        }

        Long key = new Long(subscription.getId());
        Integer valuesServed = (Integer) servings.get(key);
        valuesServed = valuesServed == null ? new Integer(0) : valuesServed;

        int i = valuesServed.intValue();

        if (i >= sequence.length) {
            sub.sendCompletion(new Completion(true));
            servings.remove(key);
            return;
        }

        int chunkLength = (int)Math.min(count, sequence.length - i);
        servings.put(key, new Integer(i + chunkLength));
        int j = 0;

        while (j < chunkLength) {
            sub.sendValue(sequence[i + j]);
            j++;
        }
    }
}
