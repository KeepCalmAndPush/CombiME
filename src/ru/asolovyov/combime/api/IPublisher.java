/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.api;

/**
 *
 * @author Администратор
 */
public interface IPublisher extends Identifiable {
    public ICancellable sink(ISubscriber subscriber);
    public IPublisher to(IOperator operator);
}
