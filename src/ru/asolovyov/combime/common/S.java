/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.common;

import java.util.Enumeration;
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

    public static void print(Object s) {
        System.out.print(s);
    }

    public static void println(Object s) {
        System.out.println(s);
    }

    public static void debug(Object s) {
//        System.out.print(s)
    }

    public static void sleep(long millis) {
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
        S.println("Printing array: " + arr1);
        int length = arr1.length;
        for (int i = 0; i < length; i++) {
            S.println(arr1[i]);
        }
    }

    public static Integer[] boxed(int[] primitives) {
        Integer[] boxed = new Integer[primitives.length];
        for (int i = 0; i < primitives.length; i++) {
            boxed[i] = new Integer(primitives[i]);
        }
        return boxed;
    }
}
