/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.utils;

/**
 *
 * @author Администратор
 */
public class S {
    public static void log(Object s) {
        System.out.println(s);
    }

    public static void debug(Object s) {
//        System.out.println(s);
    }

    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception ex) {
            throw new Error("SLEEP INTERRUPTED!");
        }
    }
}
