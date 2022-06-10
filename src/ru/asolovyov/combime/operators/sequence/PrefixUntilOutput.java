package ru.asolovyov.combime.operators.sequence;

import ru.asolovyov.combime.api.IPublisher;
import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.common.Demand;
import ru.asolovyov.combime.common.Sink;
import ru.asolovyov.combime.operators.Operator;

public class PrefixUntilOutput extends Operator {

    private boolean shouldDrop = false;

    public PrefixUntilOutput(IPublisher publisher) {
        super();
        publisher.sink(new Sink() {

            protected void onValue(Object value) {
                PrefixUntilOutput.this.shouldDrop = true;
            }

            protected void onCompletion(Completion completion) {
                if (!shouldDrop) {
                    PrefixUntilOutput.this.sendCompletion(completion);
                }
            }
        });
    }

    protected Demand _receiveInput(Object input) {
        if (this.shouldDrop) {
            this.sendCompletion(new Completion(true));
            return Demand.NONE;
        }
        return super._receiveInput(input);
    }
}
