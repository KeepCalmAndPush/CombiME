/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.operators.mapping;

import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.common.Demand;

/**
 *
 * @author Администратор
 */
public abstract class TryMap extends Map {
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
