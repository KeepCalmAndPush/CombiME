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
public class StringBinding extends PassthroughSubjectValueWrapper {
    public StringBinding(String value) {
        super(new CurrentValueSubject(value));
    }

    private StringBinding(IPublisher source) {
        super(source);
    }

    public String getString() {
        return (String)this.getValue();
    }

    public void setString(String value) {
        this.sendValue(value);
    }

    public IPublisher to(IOperator operator) {
        return new StringBinding(super.to(operator));
    }
}
