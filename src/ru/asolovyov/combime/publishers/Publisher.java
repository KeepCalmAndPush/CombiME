/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.asolovyov.combime.publishers;

import java.util.Enumeration;
import java.util.Vector;

import ru.asolovyov.combime.api.IOperator;
import ru.asolovyov.combime.api.IPublisher;
import ru.asolovyov.combime.api.ISubject;
import ru.asolovyov.combime.api.ISubscriber;
import ru.asolovyov.combime.api.ISubscription;
import ru.asolovyov.combime.api.ISubscriptionDelegate;
import ru.asolovyov.combime.api.Identifiable;
import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.common.Demand;
import ru.asolovyov.combime.common.S;
import ru.asolovyov.combime.common.Subscription;
import ru.asolovyov.combime.debugging.HandleEvents;
import ru.asolovyov.combime.debugging.Print;
import ru.asolovyov.combime.operators.combining.CombineLatest;
import ru.asolovyov.combime.operators.combining.Merge;
import ru.asolovyov.combime.operators.combining.Zip;
import ru.asolovyov.threading.DummyScheduler;
import ru.asolovyov.threading.Scheduler;

/**
 *
 * @author Администратор
 */
public abstract class Publisher implements IPublisher, ISubscriptionDelegate, Identifiable {

    protected Vector subscriptions = new Vector();
    protected Vector printers = new Vector();
    protected Vector eventHandlers = new Vector();
    private boolean hasServedValues = false;
    private boolean isCompleted = false;
    private ISubject printSubject;

    protected ISubject getPrintSubject() {
        return printSubject;
    }
    private Scheduler receptionScheduler = new DummyScheduler();
    private static long ID_COUNTER = 0;
    private long id;

    {
        generateId();
    }

    private synchronized void generateId() {
        id = Publisher.ID_COUNTER++;
    }

    public long getId() {
        return id;
    }

    protected ISubscription createSubscription(ISubscriber subscriber) {
        Subscription subscription = new Subscription(subscriber);
        subscription.setDelegate(this);
        S.debug("Subscription created: " + subscription.getId());
        sendValueToPrint("receive subscription: " + subscription);
        for (int i = 0; i < this.eventHandlers.size(); i++) {
            ((HandleEvents) this.eventHandlers.elementAt(i)).receiveSubscription(subscription);
        }
        return subscription;
    }

    public ISubscription sink(final ISubscriber subscriber) {
        ISubscription subscription = createSubscription(subscriber);
        subscriptions.addElement(subscription);
        subscriber.receiveSubscription(subscription);

        return subscription;
    }

    public IPublisher to(IOperator operator) {
        S.debug(this.getId() + " PUB TO " + operator.getId());
        sink(operator);
        return operator;
    }

    public String toString() {
        return super.toString() + " subscriptions: " + subscriptions.size();
    }

    public void subscriptionDidCancel(ISubscription subscription) {
        subscriptions.removeElement(subscription);
        sendValueToPrint("receive cancel for: " + subscription);
        for (int i = 0; i < eventHandlers.size(); i++) {
            ((HandleEvents) eventHandlers.elementAt(i)).receiveCancel(subscription);
        }
    }

    public void subscriptionDidRequestValues(ISubscription subscription, Demand demand) {
        sendValueToPrint("request: " + demand);
        for (int i = 0; i < eventHandlers.size(); i++) {
            ((HandleEvents) eventHandlers.elementAt(i)).receiveDemand(subscription, demand);
            ;
        }
    }

    public void subscriptionDidSendValue(ISubscription subscription, Object value) {
        hasServedValues = true;
        sendValueToPrint("receive value: " + value);
        for (int i = 0; i < eventHandlers.size(); i++) {
            ((HandleEvents) eventHandlers.elementAt(i)).receiveOutput(subscription, value);
            ;
        }
    }

    public void subscriptionDidSendCompletion(ISubscription subscription, Completion completion) {
        isCompleted = true;
        subscriptions.removeElement(subscription);
        sendValueToPrint("receive completion: " + completion);
        for (int i = 0; i < eventHandlers.size(); i++) {
            ((HandleEvents) eventHandlers.elementAt(i)).receiveCompletion(subscription, completion);
        }
    }

    public IPublisher receiveOn(Scheduler scheduler) {
        this.receptionScheduler = scheduler;
        return this;
    }

    public Scheduler getReceptionScheduler() {
        return this.receptionScheduler;
    }

    public static IPublisher merge(IPublisher[] publishers) {
        return new Merge(publishers);
    }

    public IPublisher merge(IPublisher publisher) {
        return new Merge(new IPublisher[]{this, publisher});
    }

    public static IPublisher combineLatest(IPublisher[] publishers) {
        return new CombineLatest(publishers);
    }

    public IPublisher combineLatest(IPublisher publisher) {
        return new CombineLatest(new IPublisher[]{this, publisher});
    }

    public static IPublisher zip(IPublisher[] publishers) {
        return new Zip(publishers);
    }

    public IPublisher zip(IPublisher publisher) {
        return new Zip(new IPublisher[]{this, publisher});
    }

    public IPublisher print(Print print) {
        this.printers.addElement(print);
        return this;
    }

    public IPublisher handleEvents(HandleEvents handler) {
        this.eventHandlers.addElement(handler);
        return this;
    }

    private void sendValueToPrint(Object value) {
        Enumeration elements = this.printers.elements();
        while (elements.hasMoreElements()) {
            ISubscriber printer = (ISubscriber) elements.nextElement();
            printer.receiveInput(value);
        }
    }

    public boolean hasServedValues() {
        return hasServedValues;
    }

    public boolean isCompleted() {
        return isCompleted;
    }
}
