package ru.asolovyov.combime.operators.errorhandling;

import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.common.Demand;
import ru.asolovyov.combime.operators.Operator;

public class Retry extends Operator {

    private int retriesCountLeft = 0;

    public Retry(int count) {
        super();
        this.retriesCountLeft = count;
    }

    protected void _receiveCompletion(Completion completion) {
        if (completion.isSuccess() || this.retriesCountLeft-- == 0) {
            super._receiveCompletion(completion);
            return;
        }

        Demand demand = this.requestsLeft.copy();
        this.requestsLeft.decrement();

        this.subscription.requestValues(demand);
    }
}
