/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.operators;

import ru.asolovyov.combime.common.Completion;

/**
 *
 * @author Администратор
 */
public class ReplaceError extends Operator {
    private Object replacement;
    
    public ReplaceError(Object replacement) {
        this.replacement = replacement;
    }

    public void receiveCompletion(Completion completion) {
        if (completion.getFailure() != null) {
            sendValue(replacement);
            return;
        }

        super.receiveCompletion(completion);
    }
}
