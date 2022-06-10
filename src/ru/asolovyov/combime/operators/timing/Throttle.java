package ru.asolovyov.combime.operators.timing;

import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.common.Demand;
import ru.asolovyov.combime.common.S;
import ru.asolovyov.combime.operators.Operator;
import ru.asolovyov.threading.DispatchQueue;
import ru.asolovyov.threading.Scheduler;

public class Throttle extends Operator {

    private boolean latest = true;
    private boolean hasInputToSend = false;
    private Object inputToSend = null;
    private Scheduler throtller;

    public Throttle(final long intervalMillis) {
        this(intervalMillis, true);
    }

    public Throttle(final long intervalMillis, boolean latest) {
        this(intervalMillis, latest, new DispatchQueue(1));
    }

    public Throttle(final long intervalMillis, boolean latest, Scheduler scheduler) {
        super();
        this.latest = latest;
        this.throtller = scheduler;
        this.throtller.schedule(new Runnable() {

            public void run() {
                while (!Throttle.this.isCompleted()) {
                    S.sleep(intervalMillis);
                    if (Throttle.this.hasInputToSend) {
                        Throttle.this.sendValue(inputToSend);
                        Throttle.this.hasInputToSend = false;
                    }
                }
            }
        });
    }

    protected Demand _receiveInput(Object input) {
        if (this.latest) {
            this.inputToSend = input;
        } else {
            if (!this.hasInputToSend) {
                this.inputToSend = input;
            }
        }
        this.hasInputToSend = true;

        Demand demand = this.requestsLeft.copy();
        this.requestsLeft.decrement();
        return demand;
    }

    protected void _receiveCompletion(Completion completion) {
        if (this.hasInputToSend) {
            this.hasInputToSend = false;
            this.sendValue(this.inputToSend);
        }
        super._receiveCompletion(completion);
    }
}
