/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.tests;

import ru.asolovyov.combime.impl.Completion;
import ru.asolovyov.combime.impl.Sink;
import ru.asolovyov.combime.utils.Just;
import ru.asolovyov.combime.utils.Void;

/**
 *
 * @author Администратор
 */
public class TestJust {

    public void testJust() {
        Just j = new Just("Hello");
        j.subscribe(new Sink() {

            protected void onCompletion(Completion completion) {
                System.out.println("DONE!");
            }

            protected void onValue(Object value) {
                String s = (String)value;
                System.out.println("onNext: " + s);
            }
        });
    }

}
