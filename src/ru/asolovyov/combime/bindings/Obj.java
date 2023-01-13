/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.bindings;

import ru.asolovyov.combime.api.IOperator;
import ru.asolovyov.combime.api.IPublisher;
import ru.asolovyov.combime.subjects.CurrentValueSubject;

/**
 *
 * @author Администратор
 */
public class Obj extends CurrentValueSubjectWrapper {
    public Obj(Object value) {
        super(new CurrentValueSubject(value));
    }

    public Obj(IPublisher source) {
        super(source);
    }

    public Object getObject() {
        return this.getValue();
    }

    public void setObject(Object value) {
        this.sendValue(value);
    }

    public IPublisher to(IOperator operator) {
        return new Obj(super.to(operator));
    }
}
