/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.operators;

import ru.asolovyov.combime.common.Demand;

/**
 *
 * @author Администратор
 */
public abstract class Filter extends Operator {
    protected abstract boolean shouldKeep(Object input);

    public Demand receiveInput(Object input) {
        Object newValue = mapValue(input);
        if (shouldKeep(input)) {
            sendValue(newValue);
        }
        return Demand.UNLIMITED;
    }
}
