/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.api;

import java.io.PrintStream;
import ru.asolovyov.threading.Scheduler;

/**
 *
 * @author Администратор
 */
public interface IPublisher extends Identifiable {
    public IPublisher to(IOperator operator);
    
    public ISubscription sink(ISubscriber subscriber);
    public ISubscription route(ISubject subject);

    public IPublisher receiveOn(Scheduler scheduler);

    public IPublisher print();
    public IPublisher print(String prefix);
    public IPublisher print(String prefix, PrintStream printStream);
    
    public IPublisher merge(IPublisher publisher);
    public IPublisher combineLatest(IPublisher publisher);

    public IPublisher switchToLatest();
    public IPublisher zip(IPublisher publisher);

    public IPublisher drop(int n);
    public IPublisher prefix(int n);

    public IPublisher assertNoFailure();
    public IPublisher assertNoFailure(String message);

    public IPublisher retry(int count);

    public IPublisher compactMap();
    public IPublisher removeDuplicates();
    public IPublisher replaceEmpty(Object replacement);
    public IPublisher replaceError(Object replacement);

    public IPublisher contains(Object object);

    public IPublisher count();
    public IPublisher max();
    public IPublisher min();

    public IPublisher collect(int count);
    public IPublisher collect(long millis);
    public IPublisher collect(int count, long millis);

    public IPublisher ignoreOutput();

    public IPublisher debounce(long millis);
    public IPublisher delay(long millis);
    public IPublisher delay(long millis, Scheduler scheduler);
    public IPublisher throttle(long millis);
    public IPublisher throttle(long millis, boolean latest);
    public IPublisher throttle(long millis, boolean latest, Scheduler scheduler);
    public IPublisher timeout(long millis);
    public IPublisher timeout(long millis, Scheduler scheduler);
}
