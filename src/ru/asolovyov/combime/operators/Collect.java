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

public class Collect extends Operator {
    private int count = 0;
    private Object[] accumulator;
    private int collectedSoFar = 0;

    public Collect(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Collect: count must not be less than 1");
        }
        this.count = count;
        this.accumulator = new Object[count];
    }

    public Demand receiveInput(Object input) {
        if (collectedSoFar == count) {
            sendValue(accumulator);
            accumulator = new Object[count];
            collectedSoFar = 0;
        }

        accumulator[collectedSoFar] = input;
        collectedSoFar++;

        return Demand.UNLIMITED;
    }

    public void receiveCompletion(Completion completion) {
        int residual = collectedSoFar % count;
        if (completion.isSuccess() && residual > 0) {
            Object[] tail = new Object[residual];
            System.arraycopy(accumulator, 0, tail, 0, residual);
            sendValue(tail);
            accumulator = new Object[count];
            collectedSoFar = 0;
        }
        super.receiveCompletion(completion);
    }
}
