/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.operators.filtering;

import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.common.Demand;

/**
 *
 * @author Администратор
 */
public class TryCompactMap extends CompactMap {
    public Demand receiveInput(Object input) {
        try {
            return super.receiveInput(input);
        } catch(Exception e) {
            sendCompletion(new Completion(e));
            subscription.cancel();
            return Demand.NONE;
        }
    }
}
