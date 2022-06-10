package ru.asolovyov.combime.operators.errorhandling;

import ru.asolovyov.combime.api.IPublisher;
import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.operators.Operator;

public abstract class Catch extends Operator {

    protected abstract IPublisher publisherForFailure(Completion completion);

    protected void _receiveCompletion(Completion completion) {
        if (completion.isSuccess()) {
            super._receiveCompletion(completion);
            return;
        }

        IPublisher publisher = this.publisherForFailure(completion);
        publisher.sink(this).connect();
    }
}
