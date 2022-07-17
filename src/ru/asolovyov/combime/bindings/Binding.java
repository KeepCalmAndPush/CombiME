/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.bindings;

/**
 *
 * @author Администратор
 */
public class Binding {
    public static Bool Bool(boolean value) {
        return new Bool(value);
    }

    public static IntBinding Int(int value) {
        return new IntBinding(value);
    }

    public static StringBinding String(String value) {
        return new StringBinding(value);
    }

    public static ObjectBinding Object(Object value) {
        return new ObjectBinding(value);
    }

    public static ArrayBinding Array(Object[] value) {
        return new ArrayBinding(value);
    }
}
