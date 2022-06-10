/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.asolovyov.combime.operators.mapping;

import ru.asolovyov.combime.operators.Operator;

/**
 *
 * @author Администратор
 */
public abstract class Map extends Operator {

    public abstract Object mapValue(Object value);
}
