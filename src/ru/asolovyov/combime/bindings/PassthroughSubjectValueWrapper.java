/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.bindings;

import ru.asolovyov.combime.api.IPublisher;
import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.common.Sink;
import ru.asolovyov.combime.subjects.CurrentValueSubject;

/**
 *
 * @author Администратор
 */
public class PassthroughSubjectValueWrapper extends CurrentValueSubject {
    protected IPublisher subject;

    public PassthroughSubjectValueWrapper(IPublisher wrappee) {
        super(null);
        this.subject = wrappee;
        this.subject.sink(new Sink() {
            protected void onValue(Object value) {
                sendValue(value);
            }
            
            protected void onCompletion(Completion completion) {
                sendCompletion(completion);
            }
        });
    }
}
