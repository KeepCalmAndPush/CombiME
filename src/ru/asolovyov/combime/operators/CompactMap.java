/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.operators;

import ru.asolovyov.combime.common.Demand;

/**
 *
 * @author Администратор
 */
public class CompactMap extends Map {
    public Demand receiveInput(Object input) {
        if (input != null) {
           return super.receiveInput(input);
        }
        return Demand.UNLIMITED;
    }

    public Object mapValue(Object value) {
        return value;
    }
}
