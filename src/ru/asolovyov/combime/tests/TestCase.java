/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.tests;

import ru.asolovyov.combime.api.ICancellable;
import ru.asolovyov.combime.api.IOperator;
import ru.asolovyov.combime.api.IPublisher;
import ru.asolovyov.combime.api.ISubscriber;
import ru.asolovyov.combime.api.ISubscription;
import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.subjects.CurrentValueSubject;
import ru.asolovyov.combime.common.Demand;
import ru.asolovyov.combime.publishers.Publisher;

/**
 *
 * @author Администратор
 */
public abstract class TestCase extends Publisher {
    private String name;
    protected CurrentValueSubject subject = new CurrentValueSubject(null);

    public TestCase(String name) {
        super();
        this.name = name;
        doTest();
    }

    public void succeed() {
        succeed(null);
    }

    public void fail() {
        fail(null);
    }

    public void succeed(String nullableComment) {
        String result = "TEST " + name + " OK";
        if (nullableComment != null) {
            result += ": " + nullableComment;
        }
        subject.sendValue(result);
        subject.sendCompletion(new Completion(true));
    }

    public void fail(String nullableReason) {
        String result = "TEST " + name + " FAILED";
        if (nullableReason != null) {
            result += ": " + nullableReason;
        }
        subject.sendValue(result);
        subject.sendCompletion(new Completion(false));
    }

    private void doTest() {
        test();
    }

    protected abstract void test();

    public ICancellable sink(ISubscriber subscriber) {
        ICancellable subscription = subject.sink(subscriber);
        return subscription;
    }

    public IPublisher to(IOperator operator) {
        return subject.to(operator);
    }

    public void subscriptionDidRequestValues(ISubscription subscription, Demand demand) {
        subject.subscriptionDidRequestValues(subscription, demand);
    }

    public void subscriptionDidCancel(ISubscription subscription) {
        subject.subscriptionDidCancel(subscription);
    }
}
