/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.asolovyov.combime.operators.filtering;

import ru.asolovyov.combime.common.Demand;
import ru.asolovyov.combime.common.S;
import ru.asolovyov.combime.operators.Operator;

/**
 *
 * @author Администратор
 */
public class RemoveDuplicates extends Operator {

    private Object latestValue = null;

    protected boolean areEqual(Object object1, Object object2) {
        if (object1 != null && object2 != null && object1 instanceof Object[] && object2 instanceof Object[]) {
            boolean equal = S.arraysEqual((Object[]) object1, (Object[]) object2);
            S.println(S.toString(this) + " REMOVE DUPLICATES ARR " + S.toString((Object[]) object1) + " vs " + S.toString((Object[]) object2) + " STOP: " + equal);
            return equal;
        }

        S.println(S.toString(this) + " REMOVE DUPLICATES " + S.toString(object1) + " " + S.toString(object2));

        if (object1 == object2) {
            S.println(S.toString(this) + " REMOVE DUPLICATES object1 == object2 STOP!");
            return true;
        }

        if (object1 != null && object2 != null && object1 instanceof Object[] && object2 instanceof Object[]) {
            boolean equal = S.arraysEqual((Object[]) object1, (Object[]) object2);
            S.println(S.toString(this) + " REMOVE DUPLICATES object1 instanceof Object[] && object2 instanceof Object[] STOP! " + equal);
            return equal;
        }

        if (object1 != null && object1.equals(object2)) {
            S.println(S.toString(this) + " REMOVE DUPLICATES object1 != null && object1.equals(object2) STOP!");
            return true;
        }

        S.println(S.toString(this) + " REMOVE DUPLICATES object1 != null && object1.equals(object2) PASS!");

        return false;
    }

    public void sendValue(Object value) {
        S.println(S.toString(this) + " REMOVE DUPLICATES _receiveInput. OLD: " + S.toString(this.latestValue) + "; NEW: " + S.toString(value));
        if (areEqual(this.latestValue, value)) {
            return;
        }
        this.latestValue = value;
        super.sendValue(value);
    }


    
//    protected Demand _receiveInput(Object input) {
//        S.println(S.toString(this) + " REMOVE DUPLICATES _receiveInput. OLD: " + S.toString(this.latestValue) + "; NEW: " + S.toString(input));
//
//        Demand demand = this.requestsLeft.copy();
//        this.requestsLeft.decrement();
//
//        if (areEqual(this.latestValue, input)) {
//            return demand;
//        }
//
//        S.println(S.toString(this) + " REMOVE DUPLICATES WILL SET LATEST VALUE TO: " + S.toString(input));
//        this.latestValue = input;
//
//        Object newValue = mapValue(input);
//        sendValue(newValue);
//        return demand;
//    }
}
