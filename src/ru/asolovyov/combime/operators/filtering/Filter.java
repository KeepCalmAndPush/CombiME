/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.asolovyov.combime.operators.filtering;

import ru.asolovyov.combime.common.Demand;
import ru.asolovyov.combime.operators.Operator;

/**
 *
 * @author Администратор
 */
public abstract class Filter extends Operator {

    protected abstract boolean shouldKeep(Object input);

    protected Demand _receiveInput(Object input) {
        Object newValue = mapValue(input);
        if (shouldKeep(input)) {
            sendValue(newValue);
        }
        return Demand.UNLIMITED;
    }
}
