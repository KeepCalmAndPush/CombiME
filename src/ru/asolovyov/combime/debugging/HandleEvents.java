package ru.asolovyov.combime.debugging;

import ru.asolovyov.combime.api.ISubscription;
import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.common.Demand;

public class HandleEvents {

    public void receiveSubscription(ISubscription subscription) {
    }

    public void receiveDemand(ISubscription subscription, Demand demand) {
    }

    public void receiveOutput(ISubscription subscription, Object value) {
    }

    public void receiveCompletion(ISubscription subscription, Completion completion) {
    }

    public void receiveCancel(ISubscription subscription) {
    }
}
