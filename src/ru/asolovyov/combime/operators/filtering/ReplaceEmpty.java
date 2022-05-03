/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.operators.filtering;

import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.common.Demand;
import ru.asolovyov.combime.operators.Operator;

/**
 *
 * @author Администратор
 */
public class ReplaceEmpty extends Operator {
    private Object replacement;
    private boolean inputReceived = false;

    public ReplaceEmpty(Object replacement) {
        this.replacement = replacement;
    }

    public Demand receiveInput(Object input) {
        inputReceived = true;
        return super.receiveInput(input);
    }

    public void receiveCompletion(Completion completion) {
        if (!inputReceived) {
            sendValue(replacement);
        }
        super.receiveCompletion(completion);
    }
}
