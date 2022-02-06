package ru.asolovyov.combime.impl;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



/**
 *
 * @author Администратор
 */
public final class Demand {
    public static final Demand NONE = new Demand(0);
    public static final Demand UNLIMITED = new Demand(-1, true);

    private long value = 0;

    public Demand(long value) {
        this(value, false);
    }

    private Demand(long value, boolean isPrivate) {
        super();
        if (!isPrivate && value < 0) {
            throw new IllegalArgumentException(
                    "Demand MUST be >= 0. Received: " + value
                    );
        }
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    public void add(Demand demand) {
        if (this == Demand.UNLIMITED) {
            return;
        }

        value += demand.getValue();
    }
    
    public long decrement() {
        return --value;
    }
}
