/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.asolovyov.combime.tests;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.midlet.*;
import ru.asolovyov.combime.api.ICancellable;
import ru.asolovyov.combime.api.IPublisher;
import ru.asolovyov.combime.api.ISubject;
import ru.asolovyov.combime.api.ISubscription;
import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.common.Demand;
import ru.asolovyov.combime.operators.Merge;
import ru.asolovyov.combime.subjects.PassthroughSubject;
import ru.asolovyov.combime.operators.Reduce;
import ru.asolovyov.combime.operators.Scan;
import ru.asolovyov.combime.common.Sink;
import ru.asolovyov.combime.publishers.Empty;
import ru.asolovyov.combime.publishers.Fail;
import ru.asolovyov.combime.publishers.Future;
import ru.asolovyov.combime.publishers.Just;
import ru.asolovyov.combime.operators.Map;
import ru.asolovyov.combime.common.S;
import ru.asolovyov.combime.common.Subscriber;
import ru.asolovyov.combime.common.Task;
import ru.asolovyov.combime.publishers.Publisher;
import ru.asolovyov.combime.publishers.Sequence;

/**
 * @author Администратор
 */
public class Tests extends MIDlet {
    private Display display;
    private Form form = new Form("CombiME-Test");
    private IPublisher merge;
    private ICancellable canc;

    public void startApp() {
        display = Display.getDisplay(this);
        display.setCurrent(form);

        S.log("TESTS STARTED");

        IPublisher tests[] = {
            this.testJust,
            this.testEmpty,
            this.testFail,
            this.testReduce,
            this.testCancel,
            this.testFutureMap,
            this.testScan,
            this.testMergeReduce,
            this.testSequence,
            this.testMap
        };

        merge = Publisher.merge(tests);
        canc = merge.sink(new Sink() {
            protected void onValue(Object value) {
                S.log(value);
                form.append(value + "\n");
            }

            protected void onCompletion(Completion completion) {
                S.log("LE FIN!");
            }
        });
    }

    private IPublisher testCancel = new TestCase("CANCEL") {
        Future future;
        private Object result;
        ICancellable cancelable;
        Thread assertThread;

        protected void test() {
            future = new Future(new Task() {
                Thread t = new Thread(new Runnable() {
                    public void run() {
                        S.sleep(1000);
                        sendValue("hello!");
                    }
                });

                public void run() {
                    t.start();
                }
            });

            cancelable = future.sink(new Sink() {
                protected void onValue(Object value) {
                    result = value;
                }
            });

            assertThread = new Thread(new Runnable() {
                public void run() {
                    S.sleep(500);
                    cancelable.cancel();
                    S.sleep(1000);
                    if (result == null) {
                        succeed();
                    } else {
                        fail();
                    }
                }
            });

            assertThread.start();
        }
    };

    private IPublisher testJust = new TestCase("JUST") {
        protected void test() {
            (new Just("Hello COMBIME!")).sink(new Sink() {
                protected void onValue(Object value) {
                    if (value.equals("Hello COMBIME!")) {
                        succeed();
                        return;
                    }
                    fail();
                }

                protected void onCompletion(Completion completion) {
                    if (!completion.isSuccess()) {
                        fail();
                    }
                }
            });
        }
    };

    private IPublisher testMap = new TestCase("TEST RAW MAP") {
        String result = "";
        protected void test() {
            Map m = new Map() {
                public Object mapValue(Object value) {
                    return ((String)value).toUpperCase();
                }
            };

            m.to(new Map() {
                public Object mapValue(Object value) {
                    String ret = "" + value + value;
                    return ret;
                }
            }).to(new Map() {
                public Object mapValue(Object value) {
                    String ret = "!" + value + "!";
                    return ret;
                }
            }).sink(new Sink() {
                protected void onValue(Object value) {
                    result = (String)value;
                }

                protected void onCompletion(Completion completion) {
                    if (completion.isSuccess() && result.equals("!AA!")) {
                        succeed();
                        return;
                    }
                    fail();
                }
            });
            m.receiveInput("a");
            m.receiveCompletion(new Completion(true));
        }
    };

    private IPublisher testFutureMap = new TestCase("FUTURE+MAP") {
        private Future future;

        protected void test() {
            future = new Future(new Task() {
            Thread t = new Thread(new Runnable() {
                public void run() {
                    S.sleep(3000);
                    sendValue("hello");
                }
            });

            public void run() {
                t.start();
            }
        });

        Map map1 = new Map() {
            public Object mapValue(Object value) {
                Object ret = "1" + value;
                return ret;
            }
        };

        Map map2 = new Map() {
            public Object mapValue(Object value) {
                String ret = value + "2";
                return ret;
            }
        };

        future.to(map1).to(map2).sink(new Sink() {
                protected void onValue(Object value) {
                    if (value.equals("1hello2")) {
                        succeed();
                        return;
                    }
                    fail();
                }
        });
        }
    };


    private IPublisher testEmpty = new TestCase("EMPTY") {
        protected void test() {
            (new Empty()).sink(new Sink() {
                protected void onValue(Object value) {
                    fail();
                }
                protected void onCompletion(Completion completion) {
                    if (completion.isSuccess()) {
                        succeed();
                    } else {
                        fail();
                    }
                }
            });
        }
    };

    private IPublisher testFail = new TestCase("FAIL") {
        protected void test() {
            (new Fail()).sink(new Sink() {

                protected void onValue(Object value) {
                    fail();
                }

                protected void onCompletion(Completion completion) {
                    if (completion.isSuccess()) {
                        fail();
                    } else {
                        succeed();
                    }
                }
            });
        }
    };

    private IPublisher testReduce = new TestCase("REDUCE") {
        private String result = "";

        protected void test() {
            result = "";
            ISubject subj = new PassthroughSubject();
            subj.to(new Reduce("") {
                protected Object reduce(Object subresult, Object currentValue) {
                    return (String)subresult + currentValue;
                }
            }).sink(new Sink() {
                protected void onValue(Object value) {
                    result += (String)value;
                }
                protected void onCompletion(Completion completion) {
                    if (completion.isSuccess() && result.equals("123")) {
                        succeed();
                        return;
                    }
                    fail();
                }
            });

            subj.sendValue("1");
            subj.sendValue("2");
            subj.sendValue("3");
            subj.sendCompletion(new Completion(true));
        }
    };

    private IPublisher testScan = new TestCase("SCAN") {
        private String result = "";

        protected void test() {
            result = "";
            ISubject subj = new PassthroughSubject();
            subj.to(new Scan("") {
                protected Object scan(Object subresult, Object currentValue) {
                    return (String)subresult + currentValue;
                }
            }).sink(new Sink() {
                protected void onValue(Object value) {
                    result += (String)value;
                }
                protected void onCompletion(Completion completion) {
                    if (completion.isSuccess() && result.equals("112123")) {
                        succeed();
                        return;
                    }
                    fail();
                }
            });

            subj.sendValue("1");
            subj.sendValue("2");
            subj.sendValue("3");
            subj.sendCompletion(new Completion(true));
        }
    };

    private IPublisher testMergeReduce = new TestCase("MERGE+REDUCE") {
        private String result = "";

        protected void test() {
            result = "";
            ISubject subj1 = new PassthroughSubject();
            ISubject subj2 = new PassthroughSubject();
            ISubject subj3 = new PassthroughSubject();

            (new Merge(new IPublisher[]{subj1, subj2, subj3})).to(new Reduce("") {
                protected Object reduce(Object subresult, Object currentValue) {
                    return ((String) subresult) + currentValue;
                }
            })
            .sink(new Sink() {
                protected void onValue(Object value) {
                    result += (String)value;
                }
                protected void onCompletion(Completion completion) {
                    if (completion.isSuccess() && result.equals("123")) {
                        succeed();
                        return;
                    }
                    fail();
                }
            });

            subj1.sendValue("1");
            subj2.sendValue("2");
            subj3.sendValue("3");

            subj2.sendCompletion(new Completion(true));
            subj3.sendCompletion(new Completion(true));
            subj1.sendCompletion(new Completion(true));
        }
    };

    private IPublisher testSequence = new TestCase("SEQUENCE") {

        protected void test() {
            Sequence s = new Sequence(new String[]{"1", "2", "3"});
            s.sink(
                    new Subscriber() {
                
                        Object[] result = new Object[3];
                        int i = 0;

                        public void receiveSubscription(ISubscription subscription) {
                            super.receiveSubscription(subscription);
                            subscription.requestValues(new Demand(1));
                        }

                        public Demand receiveInput(Object input) {
                            onValue(input);
                            return new Demand(1);
                        }

                        protected void onValue(Object value) {
                            result[i] = ((Object[]) value)[0];
                            i++;
                        }

                        protected void onCompletion(Completion completion) {
                            String[] etalon = new String[]{"1", "2", "3"};
                            if (completion.isSuccess() && S.arraysEqual(result, etalon)) {
                                succeed();
                                return;
                            }
                            fail();
                        }
                    }
            );
        }
    };

    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
    }

    protected void pauseApp() {
    }
}
