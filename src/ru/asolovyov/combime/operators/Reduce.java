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
public abstract class Reduce extends Operator {
    private Object result;

    public Reduce(Object initialResult) {
        this.result = initialResult;
    }

    protected abstract Object reduce(Object subresult, Object currentValue);

    public Demand receiveInput(Object input) {
        Object newValue = mapValue(input);

        result = reduce(result, newValue);
        return Demand.UNLIMITED;
    }

    public void receiveCompletion(Completion completion) {
        if (!completion.isSuccess()) {
            sendCompletion(completion);
            return;
        }

        sendValue(result);
        sendCompletion(new Completion(true));
    }
}
