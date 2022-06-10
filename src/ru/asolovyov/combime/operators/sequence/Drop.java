package ru.asolovyov.combime.operators.sequence;

import ru.asolovyov.combime.common.Demand;
import ru.asolovyov.combime.operators.Operator;

public class Drop extends Operator {

    private int currentIndex = -1;
    private int countOfFirstsToDrop = -1;

    public Drop(int countOfFirstsToDrop) {
        super();
        this.countOfFirstsToDrop = countOfFirstsToDrop;
    }

    public Demand receiveInput(Object input) {
        this.currentIndex++;

        if (this.shouldDrop(input)) {
            Demand demand = this.requestsLeft.copy();
            this.requestsLeft.decrement();

            return demand;
        }

        return super.receiveInput(input);
    }

    protected boolean shouldDrop(Object object) {
        if (this.countOfFirstsToDrop > this.currentIndex) {
            return true;
        }
        return false;
    }
}
