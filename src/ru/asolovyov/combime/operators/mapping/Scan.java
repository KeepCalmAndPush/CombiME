/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.operators.mapping;

import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.common.Demand;
import ru.asolovyov.combime.operators.Operator;

/**
 *
 * @author Администратор
 */
public abstract class Scan extends Operator {
    private Object result;

    public Scan(Object initialResult) {
        this.result = initialResult;
    }

    protected abstract Object scan(Object subresult, Object currentValue);

    public Demand receiveInput(Object input) {
        Object newValue = mapValue(input);
        
        result = scan(result, newValue);
        sendValue(result);
        
        return Demand.UNLIMITED;
    }

    public void receiveCompletion(Completion completion) {
        if (!completion.isSuccess()) {
            sendCompletion(completion);
            return;
        }
        
        sendCompletion(new Completion(true));
    }
}
