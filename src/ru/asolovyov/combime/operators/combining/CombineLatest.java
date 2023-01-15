package ru.asolovyov.combime.operators.combining;

import java.util.Enumeration;
import java.util.Hashtable;
import ru.asolovyov.combime.api.ICancellable;
import ru.asolovyov.combime.api.IPublisher;
import ru.asolovyov.combime.api.ISubscription;
import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.common.Demand;
import ru.asolovyov.combime.common.S;
import ru.asolovyov.combime.common.Sink;
import ru.asolovyov.combime.common.Subscription;
import ru.asolovyov.combime.operators.Operator;

/**
 *
 * @author Администратор
 */
public class CombineLatest extends Operator {

    private Hashtable cancellables = new Hashtable();
    private boolean valuesWasRequested = false;
    private IPublisher[] publishers;
    private int activePublishersCount = 0;
    private Object[] latestValues;

    public CombineLatest(IPublisher publisher) {
        this(new IPublisher[]{publisher});
    }

    public CombineLatest(IPublisher[] publishers) {
        super();
        this.publishers = publishers;
        this.activePublishersCount = publishers.length;

        this.latestValues = new Object[publishers.length];
        for (int i = 0; i < publishers.length; i++) {
            final int index = i;
            IPublisher publisher = publishers[i];
            final ICancellable token = publisher.sink(new Sink() {

                protected void onValue(Object value) {
                    CombineLatest.this.latestValues[index] = value;
                    CombineLatest.this.flush();
                }

                protected void onCompletion(Completion completion) {
                    CombineLatest.this.processCompletion(completion, new Integer(index));
                }
            });
            cancellables.put(new Integer(i), token);
        }
    }

    private synchronized void serveValuesIfNeeded() {
        if (this.valuesWasRequested) {
            return;
        }

        this.valuesWasRequested = true;
        this.flush();
    }

    private void processCompletion(Completion completion, Integer nullableCancellableKey) {
        cancellables.remove(nullableCancellableKey);
        activePublishersCount--;

        if (!completion.isSuccess()) {
            cancelAndClearPendingSubscriptions();
            _sendCompletion(completion);
            return;
        }

        if (activePublishersCount > 0) {
            return;
        }

        _sendCompletion(new Completion(true));
    }

    private void cancelAndClearPendingSubscriptions() {
        Enumeration elements = cancellables.elements();
        while (elements.hasMoreElements()) {
            ICancellable element = (ICancellable) elements.nextElement();
            element.cancel();
        }
        cancellables.clear();
    }

    public void sendValue(Object value) {
        this.flush();
    }

    private void flush() {
        Enumeration elements = subscriptions.elements();
        while (elements.hasMoreElements()) {
            Subscription element = (Subscription) elements.nextElement();
            Object[] values = S.copyArray(latestValues);
            element.sendValue(values);
        }
    }
    
    public void sendCompletion(Completion completion) {
        this.processCompletion(completion, new Integer(0));
    }

    private void _sendCompletion(Completion completion) {
        Enumeration elements = subscriptions.elements();
        while (elements.hasMoreElements()) {
            Subscription element = (Subscription) elements.nextElement();
            element.sendCompletion(completion);
        }
        subscriptions.removeAllElements();
    }

    public void subscriptionDidRequestValues(ISubscription subscription, Demand demand) {
        super.subscriptionDidRequestValues(subscription, demand);
        serveValuesIfNeeded();
    }
}
