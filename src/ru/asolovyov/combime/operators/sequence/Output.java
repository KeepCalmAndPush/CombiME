package ru.asolovyov.combime.operators.sequence;

import java.util.Vector;

import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.common.Demand;
import ru.asolovyov.combime.operators.Operator;

public class Output extends Operator {

    private int startIndex = -1;
    private int endIndex = -1;
    private int currentIndex = 0;
    private Vector output = new Vector();

    public Output(int at) {
        this(at, at);
    }

    public Output(int startIndex, int endIndex) {
        super();

        if (startIndex > endIndex) {
            throw new IllegalArgumentException("Output: endIndex MUST NOT be greater than startIndex");
        }

        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public Demand receiveInput(Object input) {
        if (this.currentIndex >= this.startIndex) {
            this.output.addElement(input);
        }

        if (this.currentIndex == this.endIndex) {
            Object array[] = new Object[this.output.size()];
            this.output.copyInto(array);
            this.sendValue(array);
            this.sendCompletion(new Completion(true));
            this.subscription.cancel();
            return Demand.NONE;
        }

        this.currentIndex++;

        Demand demand = this.requestsLeft.copy();
        this.requestsLeft.decrement();

        return demand;
    }
}
