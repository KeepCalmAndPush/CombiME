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
public class Int extends PassthroughSubjectValueWrapper {
    public Int(int value) {
        super(new CurrentValueSubject(new Integer(value)));
    }

    public Int(Integer value) {
        super(new CurrentValueSubject(value));
    }

    private Int(IPublisher source) {
        super(source);
    }

    public int getInt() {
        return ((Integer)this.getValue()).intValue();
    }

    public void setInt(int value) {
        this.sendValue(new Integer(value));
    }

    public IPublisher to(IOperator operator) {
        return new Int(super.to(operator));
    }
}
