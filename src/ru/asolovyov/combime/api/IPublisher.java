/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.api;

/**
 *
 * @author Администратор
 */
public interface IPublisher {
    public ICancellable subscribe(ISubscriber subscriber);
}
