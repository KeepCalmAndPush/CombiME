/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime;

/**
 *
 * @author Администратор
 */
public final class Demand {
    public static final Demand NONE = new Demand((long)0);
    public static final Demand UNLIMITED = new Demand(Long.MAX_VALUE);

    private long value = 0;

    public Demand(long value) {
        super();
        this.value = value;
    }

    public long getValue() {
        return this.value;
    }
}
