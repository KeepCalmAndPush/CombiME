/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.tests;

import ru.asolovyov.combime.impl.Completion;
import ru.asolovyov.combime.impl.Subscriber;
import ru.asolovyov.combime.impl.publisher.Just;
import ru.asolovyov.combime.misc.Void;

/**
 *
 * @author Администратор
 */
public class TestJust {

    public void testJust() {
        Just j = new Just("Hello");
        j.receiveSubscriber(new Subscriber() {

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
