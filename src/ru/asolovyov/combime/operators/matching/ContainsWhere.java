/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.asolovyov.combime.operators.matching;

import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.common.Demand;
import ru.asolovyov.combime.operators.Operator;

/**
 *
 * @author Администратор
 */
public abstract class ContainsWhere extends Operator {

    protected Demand _receiveInput(Object input) {
        if (doesSatisfy(input)) {
            sendValue(new Boolean(true));
            sendCompletion(new Completion(true));
            subscription.cancel();
            return Demand.NONE;
        }
        return Demand.UNLIMITED;
    }

    public void receiveCompletion(Completion completion) {
        if (completion.isSuccess()) {
            sendValue(new Boolean(false));
        }
        super.receiveCompletion(completion);
    }

    protected abstract boolean doesSatisfy(Object obj);
}