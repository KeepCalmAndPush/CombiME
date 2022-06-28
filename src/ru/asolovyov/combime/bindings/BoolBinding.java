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
public class BoolBinding {
    private CurrentValueSubject subject;

    public BoolBinding(boolean value) {
        this.subject = new CurrentValueSubject(new Boolean(value));
    }

    public boolean getBool() {
        return ((Boolean)this.subject.getValue()).booleanValue();
    }

    public void setBool(boolean value) {
        this.subject.sendValue(new Boolean(value));
    }

    public IPublisher getPublisher() {
        return this.subject;
    }
}
