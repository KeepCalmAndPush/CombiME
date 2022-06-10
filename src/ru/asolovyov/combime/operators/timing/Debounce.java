package ru.asolovyov.combime.operators.timing;

import java.util.Date;

import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.common.Demand;
import ru.asolovyov.combime.operators.Operator;

public class Debounce extends Operator {

    private long intervalMillis = 0;
    private long _previousInputTime = -1;
    private Object previousInput = null;

    public Debounce(long intervalMillis) {
        super();
        this.intervalMillis = intervalMillis;
    }

    protected Demand _receiveInput(Object input) {
        Object previous = this.previousInput;
        this.previousInput = input;

        if (this.maySendValue()) {
            return super._receiveInput(previous);
        }

        Demand demand = this.requestsLeft.copy();
        this.requestsLeft.decrement();

        return demand;
    }

    protected void _receiveCompletion(Completion completion) {
        if (this.hasServedValues() && !this.maySendValue()) {
            this.sendValue(this.previousInput);
        }
        super._receiveCompletion(completion);
    }

    private boolean maySendValue() {
        long time = (new Date()).getTime();

        long delta = time - this.getPreviousInputTime();
        this.setPreviousInputTime(time);

        if (delta >= this.intervalMillis) {
            return true;
        }

        return false;
    }

    private long getPreviousInputTime() {
        if (this._previousInputTime == -1) {
            this.setPreviousInputTime((new Date()).getTime());
        }

        return this._previousInputTime;
    }

    private void setPreviousInputTime(long time) {
        this._previousInputTime = time;
    }
}
