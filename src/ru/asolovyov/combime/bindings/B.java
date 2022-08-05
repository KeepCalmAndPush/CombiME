/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.bindings;

/**
 *
 * @author Администратор
 */
public class B {
    public static BoolBinding Bool(boolean value) {
        return new BoolBinding(value);
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
