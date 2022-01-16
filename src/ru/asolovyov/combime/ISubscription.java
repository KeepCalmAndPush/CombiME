/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime;

//import ru.asolovyov.combime.Cancellable;
/**
 *
 * @author Администратор
 */
public interface ISubscription extends Cancellable, Identifiable {
    public void requestMoreValues(Demand demand);
}
