package ru.asolovyov.combime.operators.timing;

import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.common.Demand;
import ru.asolovyov.combime.common.S;
import ru.asolovyov.combime.operators.Operator;
import ru.asolovyov.threading.DispatchQueue;
import ru.asolovyov.threading.Scheduler;

/**
 * It seems that it triggers only if there was no events in >= (timeout + 100ms)
 * 
 * @author asolovyov
 *
 */
public final class Timeout extends Operator {

    private Scheduler scheduler;
    private boolean shouldFail = false;

    public Timeout(final long millis) {
        this(millis, new DispatchQueue(1));
    }

    public Timeout(final long millis, Scheduler scheduler) {
        super();
        this.scheduler = scheduler;
        this.scheduler.schedule(new Runnable() {

            public void run() {
                while (!Timeout.this.shouldFail) {
                    Timeout.this.shouldFail = true;
                    S.sleep(millis);
                }

                if (Timeout.this.isCompleted()) {
                    return;
                }

                Timeout.this.sendCompletion(
                        new Completion(
                        new Exception(
                        this.toString() + " terminated due to timeout")));
            }
        });
    }

    protected Demand _receiveInput(Object input) {
        this.shouldFail = false;
        return super._receiveInput(input);
    }

    protected void _receiveCompletion(Completion completion) {
        this.shouldFail = false;
        super._receiveCompletion(completion);
    }
}
