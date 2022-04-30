/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.asolovyov.combime.tests;

import java.util.Enumeration;
import ru.asolovyov.combime.api.ICancellable;
import ru.asolovyov.combime.api.IOperator;
import ru.asolovyov.combime.api.IPublisher;
import ru.asolovyov.combime.api.ISubscriber;
import ru.asolovyov.combime.api.ISubscription;
import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.common.Demand;
import ru.asolovyov.combime.common.S;
import ru.asolovyov.combime.common.Subscription;
import ru.asolovyov.combime.publishers.Publisher;

/**
 *
 * @author Администратор
 */
public abstract class TestCase extends Publisher {
    private String name;
    private boolean hasRun = false;

    public TestCase(String name) {
        super();
        this.name = name;
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
        sendValue(result);
        sendCompletion(new Completion(true));
    }

    public void fail(String nullableReason) {
        String result = "TEST " + name + " FAILED";
        if (nullableReason != null) {
            result += ": " + nullableReason;
        }
        sendValue(result);
        sendCompletion(new Completion(false));
    }

    public void assertEqual(Object expected, Object got) {
        if (expected.equals(got)) {
            succeed();
            return;
        }
        
        fail("Expected \"" + expected + "\", got \"" + got + "\"");
    }

    protected abstract void test();

    public void subscriptionDidRequestValues(ISubscription subscription, Demand demand) {
        if (hasRun) {
            return;
        }
        hasRun = true;
        S.debug("TEST " + name + " STARTED");
        test();
    }

    public void sendValue(Object value) {
        Enumeration elements = subscriptions.elements();
        while (elements.hasMoreElements()) {
            Subscription element = (Subscription) elements.nextElement();
            element.sendValue(value);
        }
    }

    public void sendCompletion(Completion completion) {
        Enumeration elements = subscriptions.elements();
        while (elements.hasMoreElements()) {
            Subscription element = (Subscription) elements.nextElement();
            element.sendCompletion(completion);
        }
        subscriptions.removeAllElements();
    }
}
