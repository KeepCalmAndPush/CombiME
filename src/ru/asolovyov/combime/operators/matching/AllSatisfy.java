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
public abstract class AllSatisfy extends Operator {
    private boolean allSatisfy = true;
    
    public Demand receiveInput(Object input) {
        allSatisfy &= doesSatisfy(input);
        if (allSatisfy) {
            return Demand.UNLIMITED;
        }

        sendValue(new Boolean(false));
        sendCompletion(new Completion(true));
        subscription.cancel();
        return Demand.NONE;
    }

    public void receiveCompletion(Completion completion) {
        if (completion.isSuccess()) {
            sendValue(new Boolean(allSatisfy));
        }
        super.receiveCompletion(completion);
    }

    protected abstract boolean doesSatisfy(Object obj);
}