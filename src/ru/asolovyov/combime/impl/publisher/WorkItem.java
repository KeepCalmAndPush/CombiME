/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.impl.publisher;

import ru.asolovyov.combime.api.IPromise;

/**
 *
 * @author Администратор
 */
public abstract class WorkItem {
    public abstract void work(IPromise promise);
}
