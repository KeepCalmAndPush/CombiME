/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.common;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 *
 * @author Администратор
 */
public class S {
    public static abstract class ForEach {
        public ForEach(Object[] array) {
            for (int i = 0; i < array.length; i++) {
                with(array[i]);
            }
        }

        public ForEach(Vector vector) {
            Enumeration elements = vector.elements();
            while (elements.hasMoreElements()) {
                Object element = elements.nextElement();
                with(element);
            }
        }

        protected abstract void with(Object item);
    }

    public static void logk(Object s) {
        System.out.print(s);
    }

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
