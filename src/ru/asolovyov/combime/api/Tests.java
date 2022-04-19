/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.asolovyov.combime.api;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.midlet.*;
import ru.asolovyov.combime.impl.Completion;
import ru.asolovyov.combime.impl.CurrentValueSubject;
import ru.asolovyov.combime.impl.Sink;
import ru.asolovyov.combime.utils.Empty;
import ru.asolovyov.combime.utils.Fail;
import ru.asolovyov.combime.utils.Future;
import ru.asolovyov.combime.utils.Just;
import ru.asolovyov.combime.utils.Map;
import ru.asolovyov.combime.utils.S;
import ru.asolovyov.combime.utils.Task;

/**
 * @author Администратор
 */
public class Tests extends MIDlet {
    private Display display;
    private Form form = new Form("CombiME-Test");

    public void startApp() {
        display = Display.getDisplay(this);
        form.append("Hello, tests!");
        display.setCurrent(form);

//        testJust();
//        testFutureMap();
//        testMap();
//        testEmpty();
//        testFail();

//        testCancel();

        IPublisher tests[] = {
            this.testCancel,
            this.testEmpty
        };

        for (int i = 0; i < tests.length; i++) {
            IPublisher test = tests[i];
            test.sink(new Sink() {
                protected void onValue(Object value) {
                    S.log(value);
                }
            });
        }
    }

    private Future future;
    Just j;

    private ISubscriber secondSub = null;
    ICancellable s1, s2;
    private ISubject pts = null;
    private ISubscriber firstSub = new Sink() {
        protected void onValue(Object value) {
            S.log("1: " + (String) value);
        }
    };

    private IPublisher testCancel = new Future(new Task() {
        Future future = new Future(new Task() {
            Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(1000);
                        sendValue("hello!");
                    } catch (InterruptedException ex) {
                        throw new Error();
                    }
                }
            });
            public void run() {
                t.start();
            }
        });
        
        private Object result;
        ICancellable cancelable = future.sink(new Sink() {
            protected void onValue(Object value) {
                result = value;
            }
        });

        Thread assertThread = new Thread(new Runnable() {
            public void run() {
                S.sleep(500);
                cancelable.cancel();
                S.sleep(1000);
                sendValue("TEST CANCEL: " + (result == null ? "OK" : "FAILED"));
            }
        });

        public void run() {
            assertThread.start();
        }
    });

    private void testS1S2() {
        if (pts == null) {
            pts = new CurrentValueSubject("S1S2");
            s1 = pts.sink(firstSub);
        }
        if (secondSub == null) {
            secondSub = new Sink() {
                protected void onValue(Object value) {
                    S.log("2: " + (String) value);
                }
            };
        }
        s2 = pts.sink(secondSub);
    }

    private void testJust() {
        j = new Just("Hello COMBIME!");
        j.to(new Map() {
            public Object mapValue(Object value) {
                return ((String) value) + " length = " + ((String) value).length();
            }
        }).sink(new Sink() {
            protected void onValue(Object value) {
                S.log("Just SINK " + value);
            }
        });
    }

    Map mm = new Map() {
        public Object mapValue(Object value) {
            return "KEK: " + value;
        }
    };

    private void testMap() {
        mm.to(new Map() {
            public Object mapValue(Object value) {
                String ret = "ONE MORE MAP " + value;
                return ret;
            }
        }).to(new Map() {
            public Object mapValue(Object value) {
                String ret = "$$$ " + value + " $$$";
                return ret;
            }
        }).sink(new Sink() {
            protected void onValue(Object value) {
                S.log("SINK: " + value);
            }
        });
        mm.receiveInput("WTF");
    }

    ICancellable ic;
    private void testFutureMap() {
        future = new Future(new Task() {
            Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(3000);
                        sendValue("hello!");
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            });

            public void run() {
                t.start();
            }
        });

        S.log("Future id " + future.getId());

        Map map1 = new Map() {
            public Object mapValue(Object value) {
                Object ret = ((String) value).toUpperCase();
                return ret;
            }
        };

        Map map2 = new Map() {
            public Object mapValue(Object value) {
                String ret = "2 " + value;
                return ret;
            }
        };

        Sink sink = new Sink() {
            protected void onValue(Object value) {
                S.log("SINK: " + value);
            }
        };

        ic = future.to(map1).to(map2).sink(sink);
    }

    private IPublisher testEmpty = new Future(new Task() {
        public void run() {
            Empty e = new Empty();
            e.sink(new Sink() {
                protected void onValue(Object value) {
                    sendValue("TEST EMPTY: FAILED, EMPTY RETURNED VALUE");
                }
                protected void onCompletion(Completion completion) {
                    if (completion.isSuccess()) {
                        sendValue("TEST EMPTY: OK");
                    } else {
                       sendValue("TEST EMPTY: FAILED, RETURNED FAILED COMPLETION");
                    }
                }
            });
        }
    });

    private void testFail() {
        Fail e = new Fail();
        e.sink(new Sink() {

            protected void onValue(Object value) {
                S.log("FAIL FAILED!");
            }

            protected void onCompletion(Completion completion) {
                if (completion.isSuccess()) {
                    S.log("FAIL FAILED!");
                } else {
                    S.log("FAIL OK!");
                }
            }
        });
    }

    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
    }

    protected void pauseApp() {
    }
}
