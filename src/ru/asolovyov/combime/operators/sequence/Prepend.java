package ru.asolovyov.combime.operators.sequence;

import ru.asolovyov.combime.publishers.Sequence;
import java.util.Vector;

import ru.asolovyov.combime.api.IPublisher;
import ru.asolovyov.combime.api.ISubscription;
import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.common.Demand;
import ru.asolovyov.combime.common.S;
import ru.asolovyov.combime.common.Sink;
import ru.asolovyov.combime.operators.Operator;

public class Prepend extends Operator {

    private IPublisher publisherToPrepend;
    private boolean isPrependingPublisherCompleted = false;
    private boolean valuesWasRequested = false;
    private Vector delayedValues = new Vector();
    private Completion delayedCompletion;

    public Prepend(IPublisher publisher) {
        this.publisherToPrepend = publisher;
    }

    private synchronized void serveValuesIfNeeded() {
        if (this.valuesWasRequested) {
            return;
        }

        this.valuesWasRequested = true;

        this.publisherToPrepend.sink(new Sink() {

            protected void onValue(Object value) {
                Prepend.this.sendValue(value);
            }

            protected void onCompletion(Completion completion) {
                if (!completion.isSuccess()) {
                    Prepend.this.sendCompletion(completion);
                    return;
                }

                Prepend.this.isPrependingPublisherCompleted = true;
                Object[] array = S.toArray(Prepend.this.delayedValues);

                (new Sequence(array)).sink(Prepend.this).connect();

                if (Prepend.this.delayedCompletion != null) {
                    Prepend.this.sendCompletion(Prepend.this.delayedCompletion);
                }
            }
        });
    }

    protected Demand _receiveInput(Object input) {
        if (this.isPrependingPublisherCompleted) {
            return super._receiveInput(input);
        }

        this.delayedValues.addElement(input);
        Demand demand = this.requestsLeft.copy();
        this.requestsLeft.decrement();

        return demand;
    }

    protected void _receiveCompletion(Completion completion) {
        if (this.isPrependingPublisherCompleted) {
            super._receiveCompletion(completion);
        }

        this.delayedCompletion = completion;
    }

    public void subscriptionDidRequestValues(ISubscription subscription, Demand demand) {
        this.serveValuesIfNeeded();
        super.subscriptionDidRequestValues(subscription, demand);
    }
}
