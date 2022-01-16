/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime;

/**
 *
 * @author Администратор
 */
public abstract class Subscription implements ISubscription {
    private static long counter = 0;
    private long id;

    { generateId(); }
    
    private synchronized void generateId() {
        id = Subscription.counter++;
    }

    public long getCombimeId() {
        return id;
    }
}
