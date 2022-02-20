/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.impl.publisher;

import ru.asolovyov.combime.api.IPromise;
import ru.asolovyov.combime.api.ISubscriber;
import ru.asolovyov.combime.api.ISubscription;
import ru.asolovyov.combime.impl.subject.PassthroughSubject;

/**
 *
 * @author Администратор
 */
public class Future extends Publisher {

    public Future(WorkItem workItem) {

    }

    protected ISubscription createSubscription(ISubscriber subscriber) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}

class PromiseSubject extends PassthroughSubject implements IPromise {

    public void fulfill(Object result) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void reject(Exception exception) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
