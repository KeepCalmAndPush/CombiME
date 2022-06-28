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
public class StringBinding {
    private CurrentValueSubject subject;

    public StringBinding(String value) {
        this.subject = new CurrentValueSubject(value);
    }

    public String getString() {
        return (String)this.subject.getValue();
    }

    public void setString(String value) {
        this.subject.sendValue(value);
    }

    public IPublisher getPublisher() {
        return this.subject;
    }
}
