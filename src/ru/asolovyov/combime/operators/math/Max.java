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
public abstract class Max extends Operator {

    private Object max = null;

    protected Demand _receiveInput(Object input) {
        if (max == null) {
            max = input;
        } else {
            max = isNewValueGreater(max, input) ? input : max;
        }
        return Demand.UNLIMITED;
    }

    public void receiveCompletion(Completion completion) {
        if (completion.isSuccess()) {
            sendValue(max);
        }
        super.receiveCompletion(completion);
    }

    protected abstract boolean isNewValueGreater(Object currentMax, Object newValue);
}
