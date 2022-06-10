/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.api;

//import ru.asolovyov.combime.ICancellable;

import ru.asolovyov.combime.common.Demand;

/**
 *
 * @author Администратор
 */
public interface ISubscription extends ICancellable, Identifiable {
    public void requestValues(Demand demand);
    public ISubscriber getSubscriber();
    public void connect();
}
