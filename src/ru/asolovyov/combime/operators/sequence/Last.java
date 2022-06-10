package ru.asolovyov.combime.operators.sequence;

import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.common.Demand;
import ru.asolovyov.combime.operators.Operator;

public class Last extends Operator {

    private Object latestFitValue;
    private boolean isLatestFitValueReceived = false;

    public Demand receiveInput(Object input) {
        if (where(input)) {
            this.isLatestFitValueReceived = true;
            this.latestFitValue = input;
        }

        return super.receiveInput(input);
    }

    public void receiveCompletion(Completion completion) {
        if (completion.isSuccess() && this.isLatestFitValueReceived) {
            this.sendValue(this.latestFitValue);
        }
        super.receiveCompletion(completion);
    }

    protected boolean where(Object object) {
        return false;
    }
}
