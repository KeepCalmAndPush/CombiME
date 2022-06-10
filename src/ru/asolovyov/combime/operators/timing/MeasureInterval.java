package ru.asolovyov.combime.operators.timing;

import java.util.Date;

import ru.asolovyov.combime.api.ISubscription;
import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.common.Demand;
import ru.asolovyov.combime.operators.Operator;

public class MeasureInterval extends Operator {

    public class Interval {

        private Date date;
        private long intervalMillis;
        private Object input;
        private Completion completion;

        public Date getDate() {
            return date;
        }

        public long getIntervalMillis() {
            return intervalMillis;
        }

        public Object getInput() {
            return input;
        }

        public Completion getCompletion() {
            return completion;
        }
    }
    private Date previousInputDate;

    protected Demand _receiveInput(Object input) {
        Interval interval = this.measure();
        interval.input = input;

        return super._receiveInput(interval);
    }

    protected void _receiveCompletion(Completion completion) {
        Interval interval = this.measure();
        interval.completion = completion;

        super._receiveInput(interval);

        super._receiveCompletion(completion);
    }

    private Interval measure() {
        Date date = new Date();
        long millis = date.getTime() - this.previousInputDate.getTime();
        this.previousInputDate = date;

        Interval interval = new Interval();
        interval.date = date;
        interval.intervalMillis = millis;

        return interval;
    }

    public void subscriptionDidRequestValues(ISubscription subscription, Demand demand) {
        if (this.previousInputDate == null) {
            this.previousInputDate = (new Date());
        }

        super.subscriptionDidRequestValues(subscription, demand);
    }
}
