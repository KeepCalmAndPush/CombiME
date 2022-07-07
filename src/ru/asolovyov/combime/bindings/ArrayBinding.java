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
public class ArrayBinding extends PassthroughSubjectValueWrapper {
    public ArrayBinding(Object[] value) {
        super(new CurrentValueSubject(value));
    }

    private ArrayBinding(IPublisher source) {
        super(source);
    }

    public Object[] getArray() {
        return (Object[]) this.getValue();
    }

    public void setValue(Object[] value) {
        this.sendValue(value);
    }

    public IPublisher to(IOperator operator) {
        return new ArrayBinding(super.to(operator));
    }
}
