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
public class Str extends PassthroughSubjectValueWrapper {
    public Str(String value) {
        super(new CurrentValueSubject(value));
    }

    public Str(IPublisher source) {
        super(source);
    }

    private Str(Str source) {
        super(source);
        this.sendValue(source.getString());
    }

    public String getString() {
        return (String)this.getValue();
    }

    public void setString(String value) {
        this.sendValue(value);
    }

    public Str to(Operator operator) {
        return new Str(super.to(operator));
    }
}
