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
import ru.asolovyov.combime.impl.PassthroughSubject;
import ru.asolovyov.combime.impl.Reduce;
import ru.asolovyov.combime.impl.Scan;
import ru.asolovyov.combime.impl.Sink;
import ru.asolovyov.combime.utils.Empty;
import ru.asolovyov.combime.utils.Fail;
import ru.asolovyov.combime.utils.Future;
import ru.asolovyov.combime.utils.Just;
import ru.asolovyov.combime.utils.Map;
import ru.asolovyov.combime.utils.S;
import ru.asolovyov.combime.utils.Task;
import ru.asolovyov.combime.utils.TestCase;

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

        IPublisher tests[] = {
            this.testJust,
            this.testEmpty,
            this.testFail,
            this.testReduce,
            this.testCancel,
            this.testFutureMap,
            this.testScan
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

    private IPublisher testFutureMap = new TestCase("FUTURE + MAP") {
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
            ISubject subject1 = new PassthroughSubject();
            ISubject subject2 = new PassthroughSubject();
            ISubject subject3 = new PassthroughSubject();

            subject1.to(new Reduce("", new IPublisher[]{subject2, subject3}) {

                protected Object reduce(Object subresult, Object currentValue) {
                    String result = (String) subresult;
                    result = result + (String) currentValue;
                    return result;
                }
            }).sink(new Sink() {

                protected void onValue(Object value) {
                    result = (String) value;
                }

                protected void onCompletion(Completion completion) {
                    if (completion.isSuccess() && result.equals("123")) {
                        succeed();
                        return;
                    }
                    fail();
                }
            });

            subject1.sendValue("1");
            subject1.sendCompletion(new Completion(true));

            subject2.sendValue("2");
            subject3.sendValue("3");

            subject3.sendCompletion(new Completion(true));
            subject2.sendCompletion(new Completion(true));
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

    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
    }

    protected void pauseApp() {
    }
}
