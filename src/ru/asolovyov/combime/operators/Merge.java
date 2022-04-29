/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.operators;

import java.util.Enumeration;
import java.util.Hashtable;
import ru.asolovyov.combime.api.ICancellable;
import ru.asolovyov.combime.api.IPublisher;
import ru.asolovyov.combime.api.ISubscription;
import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.common.Demand;
import ru.asolovyov.combime.common.Sink;
import ru.asolovyov.combime.common.Subscription;
import ru.asolovyov.combime.publishers.Publisher;

/**
 *
 * @author Администратор
 */
public class Merge extends Publisher {
    private Hashtable cancellables = new Hashtable();
    private boolean valuesWasRequested = false;
    private IPublisher[] publishers;
    private int activePublishersCount = 0;

    public Merge(IPublisher publisher) {
        this(new IPublisher[]{publisher});
    }

    public Merge(IPublisher[] publishers) {
        this.publishers = publishers;
        this.activePublishersCount = publishers.length;
    }

    private synchronized void serveValuesIfNeeded() {
        if (valuesWasRequested) {
            return;
        }
        
        valuesWasRequested = true;

        for (int i = 0; i < publishers.length; i++) {
            final int index = i;
            IPublisher publisher = publishers[i];
            final ICancellable token = publisher.sink(new Sink() {
                protected void onValue(Object value) {
                    Merge.this.sendValue(value);
                }
                
                protected void onCompletion(Completion completion) {
                    Merge.this.processCompletion(completion, new Integer(index));
                }
            });
            cancellables.put(new Integer(i), token);
        }
    }

    private void processCompletion(Completion completion, Integer nullableCancellableKey) {
        cancellables.remove(nullableCancellableKey);
        activePublishersCount--;

        if (!completion.isSuccess()) {
            cancelAndClearPendingSubscriptions();
            sendCompletion(completion);
            return;
        }

        if (activePublishersCount > 0) {
            return;
        }
        
        sendCompletion(new Completion(true));
    }

    private void cancelAndClearPendingSubscriptions() {
        Enumeration elements = cancellables.elements();
        while (elements.hasMoreElements()) {
            ICancellable element = (ICancellable)elements.nextElement();
            element.cancel();
        }
        cancellables.clear();
    }

    public void sendValue(Object value) {
        Enumeration elements = subscriptions.elements();
        while (elements.hasMoreElements()) {
            Subscription element = (Subscription)elements.nextElement();
            element.sendValue(value);
        }
    }

    public void sendCompletion(Completion completion) {
        Enumeration elements = subscriptions.elements();
        while (elements.hasMoreElements()) {
            Subscription element = (Subscription)elements.nextElement();
            element.sendCompletion(completion);
        }
        subscriptions.removeAllElements();
    }

    public void subscriptionDidRequestValues(ISubscription subscription, Demand demand) {
        serveValuesIfNeeded();
    }
}
