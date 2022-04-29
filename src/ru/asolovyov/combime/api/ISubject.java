/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.api;

import ru.asolovyov.combime.common.Completion;

/**
 *
 * @author Администратор
 */
public interface ISubject extends IPublisher {
    public void sendValue(Object value);
    public void sendCompletion(Completion completion);
}
