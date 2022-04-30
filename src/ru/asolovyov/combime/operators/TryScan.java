/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.operators;

import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.common.Demand;

/**
 *
 * @author Администратор
 */
public abstract class TryScan extends Scan {
    public TryScan(Object initialResult) {
        super(initialResult);
    }

    public Demand receiveInput(Object input) {
        try {
            return super.receiveInput(input);
        } catch(Exception e) {
            subscription.cancel();
            sendCompletion(new Completion(e));
            return Demand.NONE;
        }
    }
}
