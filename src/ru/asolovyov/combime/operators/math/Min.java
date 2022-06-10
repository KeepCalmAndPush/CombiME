/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.asolovyov.combime.operators.math;

import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.common.Demand;
import ru.asolovyov.combime.operators.Operator;

/**
 *
 * @author Администратор
 */
public abstract class Min extends Operator {

    private Object min = null;

    protected Demand _receiveInput(Object input) {
        if (min == null) {
            min = input;
        } else {
            min = isNewValueLess(min, input) ? input : min;
        }
        return Demand.UNLIMITED;
    }

    public void receiveCompletion(Completion completion) {
        if (completion.isSuccess()) {
            sendValue(min);
        }
        super.receiveCompletion(completion);
    }

    protected abstract boolean isNewValueLess(Object currentMin, Object newValue);
}
