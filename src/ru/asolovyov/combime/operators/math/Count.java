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
public class Count extends Operator {
    private int count;

    public Demand receiveInput(Object input) {
        count++;
        return Demand.UNLIMITED;
    }

    public void receiveCompletion(Completion completion) {
        if (completion.isSuccess()) {
            sendValue(new Integer(count));
        }
        super.receiveCompletion(completion);
    }
}
