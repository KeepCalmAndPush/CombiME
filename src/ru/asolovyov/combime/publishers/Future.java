/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.publishers;

import ru.asolovyov.combime.api.ICancellable;
import ru.asolovyov.combime.api.IOperator;
import ru.asolovyov.combime.api.IPublisher;
import ru.asolovyov.combime.api.ISubscriber;
import ru.asolovyov.combime.common.Task;

/**
 *
 * @author Администратор
 */
public class Future extends Deferred {
    public Future(Task task) {
        super(task);
        task.run();
    }

    public ICancellable sink(ISubscriber subscriber) {
        return subject.sink(subscriber);
    }

    public IPublisher to(IOperator operator) {
        return subject.to(operator);
    }
}


