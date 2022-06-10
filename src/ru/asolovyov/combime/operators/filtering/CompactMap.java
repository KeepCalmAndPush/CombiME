/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.asolovyov.combime.operators.filtering;

import ru.asolovyov.combime.operators.mapping.Map;
import ru.asolovyov.combime.common.Demand;

/**
 *
 * @author Администратор
 */
public class CompactMap extends Map {

    protected Demand _receiveInput(Object input) {
        if (input != null) {
            return super._receiveInput(input);
        }
        return Demand.UNLIMITED;
    }

    public Object mapValue(Object value) {
        return value;
    }
}
