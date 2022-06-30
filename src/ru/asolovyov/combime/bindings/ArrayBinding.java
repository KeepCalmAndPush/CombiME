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
public class ArrayBinding {
    private CurrentValueSubject subject;

    public ArrayBinding(Object[] value) {
        this.subject = new CurrentValueSubject(value);
    }

    public Object[] getValue() {
        return (Object[]) this.subject.getValue();
    }

    public void setValue(Object[] value) {
        this.subject.sendValue(value);
    }

    public IPublisher getPublisher() {
        return this.subject;
    }
}
