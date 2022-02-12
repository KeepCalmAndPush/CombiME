package ru.asolovyov.combime.impl;

public final class Completion {
    private boolean isSuccess;
    private Exception failure;

    public Completion(boolean isSuccess, Exception failure) {
        super();
        if (isSuccess && failure != null) {
            throw new IllegalStateException(
                    "If completion isSuccess, it MUST NOT have a failure."
                    );
        }
        if (!isSuccess && failure == null) {
            throw new IllegalStateException(
                    "If completion NOT isSuccess, it MUST have a failure."
                    );
        }
        this.isSuccess = isSuccess;
        this.failure = failure;
    }

    /**
     * @return the isSuccess
     */
    public boolean isSuccess() {
        return isSuccess;
    }

    /**
     * @return the failure
     */
    public Exception getFailure() {
        return failure;
    }
}