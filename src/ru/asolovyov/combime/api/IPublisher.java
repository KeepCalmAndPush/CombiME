/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.api;

import ru.asolovyov.threading.Scheduler;

/**
 *
 * @author Администратор
 */
public interface IPublisher extends Identifiable {
    public ISubscription sink(ISubscriber subscriber);
    public IPublisher to(IOperator operator);
    
	public IPublisher receiveOn(Scheduler scheduler);
}
