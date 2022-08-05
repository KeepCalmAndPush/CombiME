/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.bindings;

import ru.asolovyov.combime.api.IPublisher;
import ru.asolovyov.combime.operators.Operator;
import ru.asolovyov.combime.subjects.CurrentValueSubject;

/**
 *
 * @author Администратор
 */
public class StringBinding extends PassthroughSubjectValueWrapper {
    public StringBinding(String value) {
        super(new CurrentValueSubject(value));
    }

    public StringBinding(IPublisher source) {
        super(source);
    }

    private StringBinding(StringBinding source) {
        super(source);
        this.sendValue(source.getString());
    }

    public String getString() {
        return (String)this.getValue();
    }

    public void setString(String value) {
        this.sendValue(value);
    }

    public StringBinding to(Operator operator) {
        return new StringBinding(super.to(operator));
    }
}
