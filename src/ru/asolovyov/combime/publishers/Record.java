package ru.asolovyov.combime.publishers;

import java.util.Enumeration;
import java.util.Vector;

import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.common.S;
import ru.asolovyov.combime.common.Sink;
import ru.asolovyov.combime.operators.Operator;
import ru.asolovyov.combime.operators.timing.MeasureInterval;
import ru.asolovyov.combime.operators.timing.MeasureInterval.Interval;

public class Record extends Operator {

    private Vector intervals = new Vector();
    private boolean mayServeValues = false;
    private Operator measurer = new MeasureInterval();

    public Record() {
        super();
        this.measurer.sink(new Sink() {

            protected void onValue(Object value) {
                intervals.addElement(value);
            }

            protected void onCompletion(Completion completion) {
                serveValues();
            }
        });
    }

    public void sendValue(Object value) {
        if (!this.mayServeValues) {
            this.measurer.receiveInput(value);
            return;
        }
        super.sendValue(value);
    }

    public void sendCompletion(Completion completion) {
        if (!this.mayServeValues) {
            this.measurer.receiveCompletion(completion);
            return;
        }
        super.sendCompletion(completion);
    }

    private void serveValues() {
        this.mayServeValues = true;

        Thread server = new Thread(new Runnable() {

            public void run() {
                Enumeration e = intervals.elements();
                while (e.hasMoreElements()) {
                    Interval interval = (Interval) e.nextElement();
                    S.sleep(interval.getIntervalMillis());
                    if (interval.getInput() != null) {
                        sendValue(interval.getInput());
                    } else if (interval.getCompletion() != null) {
                        sendCompletion(interval.getCompletion());
                    }
                }
            }
        });
        server.start();
    }
}
