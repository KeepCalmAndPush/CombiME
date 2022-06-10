/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.operators.reducing;

import ru.asolovyov.combime.common.Demand;
import ru.asolovyov.combime.operators.Operator;

/**
 *
 * @author Администратор
 */
public class IgnoreOutput extends Operator {

    protected Demand _receiveInput(Object input) {
        return Demand.UNLIMITED;
    }

}
