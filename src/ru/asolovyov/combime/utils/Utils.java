/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.asolovyov.combime.utils;

/**
 *
 * @author Администратор
 */
public final class Utils {
    public static void assertIsA(String d1, Class descendant, String d2, Class ancestor) {
        if (ancestor.isAssignableFrom(descendant)) {
            return;
        }
        throw new IllegalArgumentException(d1 + " ("
                + descendant.getName()
                + ") MUST BE OF TYPE OR DESCENDANT OF "
                + d2
                + " (" + ancestor.getName() + ")");
    }
}
