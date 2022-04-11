/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.utils;

import ru.asolovyov.combime.impl.Completion;
import ru.asolovyov.combime.impl.Subscriber;

/**
 *
 * @author Администратор
 */
public class Sink extends Subscriber {
    protected void onValue(Object value) { }
    protected void onCompletion(Completion completion) { }
}
