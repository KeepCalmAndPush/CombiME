/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.bindings;

import ru.asolovyov.combime.api.IOperator;
import ru.asolovyov.combime.api.IPublisher;
import ru.asolovyov.combime.common.S;
import ru.asolovyov.combime.subjects.CurrentValueSubject;

/**
 *
 * @author Администратор
 */
public class Int extends CurrentValueSubjectWrapper {
    public Int(int value) {
        super(new CurrentValueSubject(new Integer(value)));
    }

    public Int(Integer value) {
        super(new CurrentValueSubject(value));
    }

    public Int(IPublisher source) {
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

    public String toString() {
        return "KEK " + S.stripPackageName(super.toString()) + " VALUE " + getValue();
    }
}
