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
public class RemoveDuplicates extends Operator {
    private Object latestValue;
    
    protected boolean areEqual(Object object1, Object object2) {
        if (object1 == object2) {
            return true;
        }
        
        if (object1 != null && object1.equals(object2)) {
            return true;
        }
        
        return false;
    }

    protected Demand _receiveInput(Object input) {
        if (areEqual(latestValue, input)) {
            return Demand.UNLIMITED;
        }
        latestValue = input;
        Object newValue = mapValue(input);
        sendValue(newValue);
        return Demand.UNLIMITED;
    }
}
