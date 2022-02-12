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
    public void sendValue(Object value);
}
