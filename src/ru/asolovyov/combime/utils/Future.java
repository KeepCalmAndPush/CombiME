/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.utils;

import ru.asolovyov.combime.api.ICancellable;
import ru.asolovyov.combime.api.ISubscriber;

/**
 *
 * @author Администратор
 */
public class Future extends Deferred {
    public Future(Task task) {
        super(task);
        task.run();
    }

    public ICancellable subscribe(ISubscriber subscriber) {
        return subject.subscribe(subscriber);
    }
}


