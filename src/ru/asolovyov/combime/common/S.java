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
    public static boolean isDebugEnabled = false;

    public static abstract class Delay {

        Thread thread;

        public Delay(final long millis) {
            thread = new Thread(new Runnable() {
                public void run() {
                    S.sleep(millis);
                    work();
                }
            });
            thread.start();
        }

        protected abstract void work();
    }

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

    public static abstract class Filter {
        public abstract boolean filter(Object object);
    }

    public static void print(Object s) {
        System.out.print(s);
    }

    public static void println(Object s) {
        System.out.println(s);
    }

    public static void debug(Object s) {
        if (isDebugEnabled) {
            System.out.print(s);
        }
    }

    public static String stripPackageName(String string) {
        int index = string.lastIndexOf('.');
        if (index == -1) {
            return string;
        }
        if (index == string.length() - 1) {
            return "";
        }
        return string.substring(index + 1);
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
            Object element1 = arr1[i];
            Object element2 = arr2[i];
            if (element1 == null && element2 == null) {
                continue;
            }

            if (element1 == null || element2 == null) {
                return false;
            }

            if (!arr1[i].equals(arr2[i])) {
                return false;
            }
        }

        return true;
    }

    public static String arrayToString(Object[] arr1) {
        StringBuffer result = new StringBuffer();

        int length = arr1.length;
        for (int i = 0; i < length; i++) {
            result.append(arr1[i]).append(i == length - 1 ? "" : ", ");
        }

        return result.toString();
    }

    public static void printArr(Object[] arr1) {
        S.println("Printing array: " + arr1);
        int length = arr1.length;
        for (int i = 0; i < length; i++) {
            S.println(arr1[i]);
        }
        S.println("Printing array finished.\n");
    }

    public static Integer[] boxed(int[] primitives) {
        Integer[] boxed = new Integer[primitives.length];
        for (int i = 0; i < primitives.length; i++) {
            boxed[i] = new Integer(primitives[i]);
        }
        return boxed;
    }

    public static Object[] filter(Object[] objects, Filter filter) {
        Vector kept = new Vector();
        for (int i = 0; i < objects.length; i ++) {
            Object object = objects[i];
            if (filter.filter(object)) {
                kept.addElement(object);
            }
        }
        return S.toArray(kept);
    }

    public static void wait(Object o) {
        S.wait(o, 0);
    }

    public static void wait(Object o, long timeout) {
        try {
            synchronized (o) {
                o.wait(timeout);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void notify(Object o) {
        synchronized (o) {
            o.notify();
        }
    }

    public static void notifyAll(Object o) {
        synchronized (o) {
            o.notifyAll();
        }
    }

    public static Object[] toArray(Vector vector) {
        Object[] array = new Object[vector.size()];
        vector.copyInto(array);
        return array;
    }

    public static boolean contains(int i, int[] arr) {
        for (int j = 0; j < arr.length; j++) {
            int k = arr[j];
            if (k == i) {
                return true;
            }
        }

        return false;
    }

    public static boolean contains(char i, char[] arr) {
        for (int j = 0; j < arr.length; j++) {
            char k = arr[j];
            if (k == i) {
                return true;
            }
        }

        return false;
    }

    public static boolean contains(String i, String[] arr) {
        for (int j = 0; j < arr.length; j++) {
            String k = arr[j];
            if (k == null ? i == null : k.equals(i)) {
                return true;
            }
        }

        return false;
    }
}
