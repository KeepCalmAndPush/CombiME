/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.asolovyov.combime.impl;

import java.util.Vector;
import ru.asolovyov.combime.api.ICancellable;
import ru.asolovyov.combime.api.IPublisher;

/**
 *
 * @author Администратор
 */
public abstract class Reduce extends Operator {
    private Object result;
    private IPublisher[] publishers;
    private Vector cancellables = new Vector();

    public Reduce(IPublisher publisher) {
        this(new IPublisher[]{publisher});
    }

    public Reduce(IPublisher[] publishers) {
        this.publishers = publishers;

        for (int i = 0; i < publishers.length; i++) {
            IPublisher publisher = publishers[i];
            ICancellable token = publisher.sink(new Sink() {

                protected void onValue(Object value) {
                    result = reduce(result, value);
                }

                protected void onCompletion(Completion completion) {
                    if (!completion.isSuccess()) {
                        //поканселить всё нахер
                        sendCompletion(completion);
                    }
                    //проверить что не осталось больше подписок
                    //если не осталось - кинуть успешный комплишен
                }
            });
            cancellables.addElement(token);
        }
    }

    protected abstract Object reduce(Object subresult, Object currentValue);

    public Demand receiveInput(Object input) {
        Demand demand = super.receiveInput(input);
        this.result = this.reduce(this.result, input);
        return demand;
    }

    public void receiveCompletion(Completion completion) {
        if (!completion.isSuccess()) {
            //поканселить всё нахер
            this.sendCompletion(completion);
        }
        //проверить что не осталось больше подписок
        //если не осталось - кинуть успешный комплишен
    }
}
