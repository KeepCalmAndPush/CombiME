/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.asolovyov.combime.tests;

import java.util.Enumeration;
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
    public final class Result {
        private String message;
        private boolean passed;

        public Result(String message, boolean passed) {
            this.message = message;
            this.passed = passed;
        }

        public String getMessage() {
            return message;
        }
        
        public boolean passed() {
            return passed;
        }
    }

    private String name;
    private boolean hasRun = false;

    public TestCase(String name) {
        super();
        this.name = name;
    }

    public void pass() {
        pass(null);
    }

    public void fail() {
        fail(null);
    }

    public void pass(String nullableComment) {
        String result = "TEST " + name + " OK";
        if (nullableComment != null) {
            result += ": " + nullableComment;
        }
        sendValue(new Result(result, true));
        sendCompletion(new Completion(true));
    }

    public void fail(String nullableReason) {
        String result = "TEST " + name + " FAILED";
        if (nullableReason != null) {
            result += ": " + nullableReason;
        }
        sendValue(new Result(result, false));
        sendCompletion(new Completion(true));
    }

    public void assertEqual(Object expected, Object got) {
        if (expected == got) {
            return;
        }
        
        if (expected != null && expected.equals(got)) {
            return;
        }
        
        fail("Expected \"" + expected + "\", got \"" + got + "\"");
    }

    public void assertEqual(boolean b1, boolean b2) {
        if (b1 == b2) {
            return;
        }

        fail("Expected \"" + b1 + "\", got \"" + b2 + "\"");
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
