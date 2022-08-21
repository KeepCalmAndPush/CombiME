/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.bindings;

import ru.asolovyov.combime.api.IPublisher;
import ru.asolovyov.combime.operators.Operator;
import ru.asolovyov.combime.operators.mapping.Map;
import ru.asolovyov.combime.subjects.CurrentValueSubject;

/**
 *
 * @author Администратор
 */
public class Bool extends PassthroughSubjectValueWrapper {
    private Bool invertedBinding = null;

    public Bool(boolean value) {
       super(new CurrentValueSubject(new Boolean(value)));
    }

    public Bool(Boolean value) {
       super(new CurrentValueSubject(value));
    }

    private Bool(IPublisher source) {
        super(source);
    }

    public boolean getBool() {
        return ((Boolean)this.getValue()).booleanValue();
    }

    public void setBool(boolean value) {
        this.sendValue(new Boolean(value));
    }

    public Bool to(Operator operator) {
        return new Bool(super.to(operator));
    }

    public Bool inverted() {
        this.invertedBinding = this.invertedBinding != null
                ? this.invertedBinding
                : this.to(new Map() { 
                      public Object mapValue(Object value) {
                            return new Boolean(!((Boolean) value).booleanValue());
                      }
                  });

        return this.invertedBinding;
    }


}
