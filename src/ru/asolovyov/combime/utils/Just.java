/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.utils;

import ru.asolovyov.combime.api.ISubscriber;
import ru.asolovyov.combime.api.ISubscription;
import ru.asolovyov.combime.impl.Completion;
import ru.asolovyov.combime.impl.Publisher;
import ru.asolovyov.combime.impl.Subscription;

/**
 *
 * @author Администратор
 */
public class Just extends Publisher {
    private Object value;

    public Just(Object value) {
        this.value = value;
    }

    protected ISubscription createSubscription(ISubscriber subscriber) {
        return new Subscription(subscriber) {

            private boolean isEmitted = false;

            protected boolean mayEmitValue() {
                return super.mayEmitValue() && !isEmitted;
            }

            protected Object emitValue() {
                isEmitted = true;
                return value;
            }

            protected Completion emitCompletion() {
                return new Completion(true, null);
            }
        };
    }
}