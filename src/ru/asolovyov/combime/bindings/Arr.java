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
public class Arr extends CurrentValueSubjectWrapper {
    public static abstract class Enumerator {
        public abstract void onElement(Object element);
    }
    
    public Arr(Object[] value) {
        super(new CurrentValueSubject(value));
    }

    private Arr(IPublisher source) {
        super(source);
    }

    public Object[] getArray() {
        return (Object[]) this.getValue();
    }

    public void setArray(Object[] value) {
        this.sendValue(value);
    }

    public IPublisher to(IOperator operator) {
        return new Arr(super.to(operator));
    }

    public void forEach(Enumerator e) {
        Object[] elements = this.getArray();
        for (int i = 0; i < elements.length; i++) {
            Object object = elements[i];
            e.onElement(object);
        }
    }
}
