/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.bindings;

import ru.asolovyov.combime.api.IPublisher;
import ru.asolovyov.combime.subjects.CurrentValueSubject;

/**
 *
 * @author Администратор
 */
public class IntBinding {
    private CurrentValueSubject subject;

    public IntBinding(int value) {
        this.subject = new CurrentValueSubject(new Integer(value));
    }

    public int getInt() {
        return ((Integer)this.subject.getValue()).intValue();
    }

    public void setInt(int value) {
        this.subject.sendValue(new Integer(value));
    }

    public IPublisher getPublisher() {
        return this.subject;
    }
}
