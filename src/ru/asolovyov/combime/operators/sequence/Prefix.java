package ru.asolovyov.combime.operators.sequence;

import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.common.Demand;
import ru.asolovyov.combime.operators.Operator;

public class Prefix extends Operator {

    private int currentIndex = -1;
    private int length = -1;

    public Prefix(int length) {
        super();
        this.length = length;
    }

    public Demand receiveInput(Object input) {
        this.currentIndex++;

        if (this.shouldPass(input)) {
            this.sendValue(input);
            Demand demand = this.requestsLeft.copy();
            this.requestsLeft.decrement();

            return demand;
        }

        this.subscription.cancel();
        this.sendCompletion(new Completion(true));

        return Demand.NONE;
    }

    protected boolean shouldPass(Object object) {
        if (this.length > this.currentIndex) {
            return true;
        }
        return false;
    }
}
