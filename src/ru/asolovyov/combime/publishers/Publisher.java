/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.asolovyov.combime.publishers;

import java.io.PrintStream;
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
import ru.asolovyov.combime.common.Sink;
import ru.asolovyov.combime.common.Subscription;
import ru.asolovyov.combime.debugging.HandleEvents;
import ru.asolovyov.combime.debugging.Print;
import ru.asolovyov.combime.operators.combining.CombineLatest;
import ru.asolovyov.combime.operators.combining.Merge;
import ru.asolovyov.combime.operators.combining.SwitchToLatest;
import ru.asolovyov.combime.operators.combining.Zip;
import ru.asolovyov.combime.operators.errorhandling.AssertNoFailure;
import ru.asolovyov.combime.operators.errorhandling.Retry;
import ru.asolovyov.combime.operators.filtering.CompactMap;
import ru.asolovyov.combime.operators.filtering.RemoveDuplicates;
import ru.asolovyov.combime.operators.filtering.ReplaceEmpty;
import ru.asolovyov.combime.operators.filtering.ReplaceError;
import ru.asolovyov.combime.operators.matching.Contains;
import ru.asolovyov.combime.operators.math.Count;
import ru.asolovyov.combime.operators.reducing.Collect;
import ru.asolovyov.combime.operators.reducing.IgnoreOutput;
import ru.asolovyov.combime.operators.timing.Debounce;
import ru.asolovyov.combime.operators.timing.Delay;
import ru.asolovyov.combime.operators.timing.Throttle;
import ru.asolovyov.combime.operators.timing.Timeout;
import ru.asolovyov.threading.DummyScheduler;
import ru.asolovyov.threading.Scheduler;

/**
 *
 * @author Администратор
 */
public abstract class Publisher implements IPublisher, ISubscriptionDelegate, Identifiable {
    protected static Object NULL_OBJECT = new Object();
    
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

    public ISubscription route(final ISubject subject) {
        return this.sink(new Sink() {
            protected void onValue(Object value) {
                subject.sendValue(value);
            }
            protected void onCompletion(Completion completion) {
                subject.sendCompletion(completion);
            }
        });
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

    
    public IPublisher print() { return this.to(new Print()); }
    public IPublisher print(String prefix) { return this.to(new Print(prefix)); }
    public IPublisher print(String prefix, PrintStream printStream) { return this.to(new Print(prefix, printStream)); }

    public static IPublisher merge(IPublisher[] publishers) { return new Merge(publishers); }
    public IPublisher merge(IPublisher publisher) { return new Merge(new IPublisher[]{this, publisher}); }

    public static IPublisher combineLatest(IPublisher[] publishers) { return new CombineLatest(publishers); }
    public IPublisher combineLatest(IPublisher publisher) { return new CombineLatest(new IPublisher[]{this, publisher}); }

    public IPublisher switchToLatest() { return this.to(new SwitchToLatest()); }

    public static IPublisher zip(IPublisher[] publishers) {  return new Zip(publishers); }
    public IPublisher zip(IPublisher publisher) { return new Zip(new IPublisher[]{this, publisher}); }

    public IPublisher assertNoFailure() { return this.to(new AssertNoFailure()); }
    public IPublisher assertNoFailure(String message) { return this.to(new AssertNoFailure(message)); }

    public IPublisher retry(int count) { return this.to(new Retry(count)); }

    public IPublisher compactMap() { return this.to(new CompactMap()); }
    public IPublisher removeDuplicates() { return this.to(new RemoveDuplicates()); }
    public IPublisher replaceEmpty(Object replacement) { return this.to(new ReplaceEmpty(replacement)); }
    public IPublisher replaceError(Object replacement) { return this.to(new ReplaceError(replacement)); }

    public IPublisher contains(Object object) { return this.to(new Contains(object)); }

    public IPublisher count() { return this.to(new Count()); }
    public IPublisher max() { return this.to(new Count()); }
    public IPublisher min() { return this.to(new Count()); }

    public IPublisher collect(int count) { return this.to(new Collect(count)); }
    public IPublisher collect(long millis) { return this.to(new Collect(millis)); }
    public IPublisher collect(int count, long millis) { return this.to(new Collect(millis, count)); }

    public IPublisher ignoreOutput() { return this.to(new IgnoreOutput()); }
    
    public IPublisher debounce(long millis) { return this.to(new Debounce(millis)); }
    public IPublisher delay(long millis) { return this.to(new Delay(millis)); }
    public IPublisher delay(long millis, Scheduler scheduler) { return this.to(new Delay(millis, scheduler)); }
    public IPublisher throttle(long millis) { return this.to(new Throttle(millis)); }
    public IPublisher throttle(long millis, boolean latest) { return this.to(new Throttle(millis, latest)); }
    public IPublisher throttle(long millis, boolean latest, Scheduler scheduler) { return this.to(new Throttle(millis, latest, scheduler)); }
    public IPublisher timeout(long millis) { return this.to(new Timeout(millis)); }
    public IPublisher timeout(long millis, Scheduler scheduler) { return this.to(new Timeout(millis, scheduler)); }
}
