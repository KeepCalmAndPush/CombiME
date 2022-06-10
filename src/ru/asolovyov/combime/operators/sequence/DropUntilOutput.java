package ru.asolovyov.combime.operators.sequence;

import ru.asolovyov.combime.api.IPublisher;
import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.common.Demand;
import ru.asolovyov.combime.common.Sink;
import ru.asolovyov.combime.operators.Operator;

public class DropUntilOutput extends Operator {

    private boolean shouldDrop = true;

    public DropUntilOutput(IPublisher publisher) {
        super();
        publisher.sink(new Sink() {

            protected void onValue(Object value) {
                DropUntilOutput.this.shouldDrop = false;
            }

            protected void onCompletion(Completion completion) {
                if (shouldDrop) {
                    DropUntilOutput.this.sendCompletion(completion);
                }
            }
        });
    }

    protected Demand _receiveInput(Object input) {
        if (this.shouldDrop) {
            Demand demand = this.requestsLeft.copy();
            this.requestsLeft.decrement();

            return demand;
        }
        return super._receiveInput(input);
    }
}
