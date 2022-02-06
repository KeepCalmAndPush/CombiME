/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.api;

/**
 *
 * @author Администратор
 */
public interface ISubject extends IPublisher {
    /** Object object MUST be of OutputType */
    public void send(Object object);
}
