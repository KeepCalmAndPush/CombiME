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
public class IgnoreOutput extends Operator {

    public Demand receiveInput(Object input) {
        return Demand.UNLIMITED;
    }

}
