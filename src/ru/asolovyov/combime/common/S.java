/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.common;

/**
 *
 * @author Администратор
 */
public class S {
    public static void log(Object s) {
        System.out.println(s);
    }

    public static void debug(Object s) {
//        System.out.println(s)
    }

    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception ex) {
            throw new Error("SLEEP INTERRUPTED!");
        }
    }

    public static boolean arraysEqual(Object[] arr1, Object[] arr2) {
        if (arr1.length != arr2.length) {
            return false;
        }

        int length = arr1.length;
        for (int i = 0; i < length; i++) {
            if (!arr1[i].equals(arr2[i])) {
                return false;
            }
        }

        return true;
    }

    public static void printArr(Object[] arr1) {
        S.log("Printing array: " + arr1);
        int length = arr1.length;
        for (int i = 0; i < length; i++) {
            S.log(arr1[i]);
        }
    }
}
