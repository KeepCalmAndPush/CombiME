package ru.asolovyov.combime.debugging;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.common.Subscriber;

public class Print extends Subscriber {

    private String prefix;
    private PrintStream printStream;

    public Print() {
        this(null, null);
    }

    public Print(String prefix) {
        this(prefix, System.out);
    }

    public Print(String prefix, PrintStream printStream) {
        super();
        this.prefix = prefix;
        if (printStream == null) {
            printStream = new PrintStream(new OutputStream() {

                public void write(int b) throws IOException {
                }
            });
        }
        this.printStream = printStream;
    }

    protected void onValue(Object value) {
        if (this.prefix == null) {
            this.printStream.println(value);
            return;
        }

        this.printStream.println(this.prefix + " " + value);
    }

    protected void onCompletion(Completion completion) {
    }
}
