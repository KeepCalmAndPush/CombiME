package ru.asolovyov.combime.operators.errorhandling;

import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.operators.Operator;

public class AssertNoFailure extends Operator {

    private String message = null;

    public AssertNoFailure() {
        super();
    }

    public AssertNoFailure(String message) {
        super();
        this.message = message;
    }

    protected void _receiveCompletion(Completion completion) {
        if (!completion.isSuccess()) {
            throw new Error(this.message);
        }
        super._receiveCompletion(completion);
    }
}
