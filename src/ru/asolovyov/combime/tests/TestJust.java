/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.tests;

import ru.asolovyov.combime.impl.Completion;
import ru.asolovyov.combime.impl.Subscriber;
import ru.asolovyov.combime.utils.Just;
import ru.asolovyov.combime.utils.Void;

/**
 *
 * @author Администратор
 */
public class TestJust {

    public void testJust() {
        Just j = new Just("Hello");
        j.receiveSubscriber(new Subscriber("".getClass(), (new Void()).getClass()) {

            protected void onNext(Object input) {
                String s = (String)input;
                System.out.println("onNext: " + s);
            }

            protected void onCompletion(Completion completion) {
                System.out.println("DONE!");
            }
        });
    }

}
