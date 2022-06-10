package ru.asolovyov.combime.operators.sequence;

import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.common.Demand;
import ru.asolovyov.combime.operators.Operator;

public abstract class First extends Operator {

    public Demand receiveInput(Object input) {
        if (where(input)) {
            sendValue(input);
            sendCompletion(new Completion(true));

            this.subscription.cancel();

            return Demand.NONE;
        }

        Demand demand = this.requestsLeft.copy();
        this.requestsLeft.decrement();

        return demand;
    }

    protected abstract boolean where(Object object);
}
