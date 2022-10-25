/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.asolovyov.combime.tests;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import ru.asolovyov.combime.api.ICancellable;
import ru.asolovyov.combime.api.IPublisher;
import ru.asolovyov.combime.api.ISubject;
import ru.asolovyov.combime.api.ISubscription;
import ru.asolovyov.combime.common.Completion;
import ru.asolovyov.combime.common.Demand;
import ru.asolovyov.combime.common.S;
import ru.asolovyov.combime.common.Sink;
import ru.asolovyov.combime.common.Subscriber;
import ru.asolovyov.combime.common.Task;
import ru.asolovyov.combime.debugging.HandleEvents;
import ru.asolovyov.combime.debugging.Print;
import ru.asolovyov.combime.operators.combining.CombineLatest;
import ru.asolovyov.combime.operators.combining.Merge;
import ru.asolovyov.combime.operators.combining.SwitchToLatest;
import ru.asolovyov.combime.operators.combining.Zip;
import ru.asolovyov.combime.operators.errorhandling.AssertNoFailure;
import ru.asolovyov.combime.operators.errorhandling.Catch;
import ru.asolovyov.combime.operators.errorhandling.Retry;
import ru.asolovyov.combime.operators.filtering.CompactMap;
import ru.asolovyov.combime.operators.filtering.Filter;
import ru.asolovyov.combime.operators.filtering.RemoveDuplicates;
import ru.asolovyov.combime.operators.filtering.ReplaceEmpty;
import ru.asolovyov.combime.operators.filtering.ReplaceError;
import ru.asolovyov.combime.operators.mapping.FlatMap;
import ru.asolovyov.combime.operators.mapping.Map;
import ru.asolovyov.combime.operators.mapping.Scan;
import ru.asolovyov.combime.operators.matching.AllSatisfy;
import ru.asolovyov.combime.operators.matching.Contains;
import ru.asolovyov.combime.operators.matching.ContainsWhere;
import ru.asolovyov.combime.operators.math.Count;
import ru.asolovyov.combime.operators.math.Max;
import ru.asolovyov.combime.operators.math.Min;
import ru.asolovyov.combime.operators.reducing.Collect;
import ru.asolovyov.combime.operators.reducing.IgnoreOutput;
import ru.asolovyov.combime.operators.reducing.Reduce;
import ru.asolovyov.combime.operators.sequence.Drop;
import ru.asolovyov.combime.operators.sequence.DropUntilOutput;
import ru.asolovyov.combime.operators.sequence.First;
import ru.asolovyov.combime.operators.sequence.Output;
import ru.asolovyov.combime.operators.sequence.Prefix;
import ru.asolovyov.combime.operators.sequence.PrefixUntilOutput;
import ru.asolovyov.combime.operators.sequence.Prepend;
import ru.asolovyov.combime.publishers.Sequence;
import ru.asolovyov.combime.operators.timing.Debounce;
import ru.asolovyov.combime.operators.timing.Delay;
import ru.asolovyov.combime.operators.timing.MeasureInterval;
import ru.asolovyov.combime.operators.timing.Throttle;
import ru.asolovyov.combime.operators.timing.Timeout;
import ru.asolovyov.combime.publishers.Empty;
import ru.asolovyov.combime.publishers.Fail;
import ru.asolovyov.combime.publishers.Future;
import ru.asolovyov.combime.publishers.Just;
import ru.asolovyov.combime.publishers.Publisher;
import ru.asolovyov.combime.publishers.Record;
import ru.asolovyov.combime.subjects.PassthroughSubject;
import ru.asolovyov.threading.DispatchQueue;

/**
 * @author Администратор
 */
public class Tests extends MIDlet {

    private Display display;
    private Form form = new Form("CombiME-Test");
    private int run = 0;
    private int passed = 0;
    private int failed = 0;

    public void startApp() {
        display = Display.getDisplay(this);
        display.setCurrent(form);

        IPublisher tests[] = {
            this.testSubRecSchedulers(),
            this.testRecord(),
            this.testAssertNoFailure(),
            this.testRetry(),
            this.testMeasureInterval(),
            this.testThrottle(),
            this.testDebounce(),
            this.testTimeout(),
            this.testPrepend(),
            this.testDrop(),
            this.testFirst(),
            this.testOutput(),
            this.testPrefix(),
            this.testJust(),
            this.testEmpty(),
            this.testFail(),
            this.testReduce(),
            this.testCancel(),
            this.testFutureMap(),
            this.testScan(),
            this.testMergeReduce(),
            this.testSequence(),
            this.testMap(),
            this.testCompactMap(),
            this.testTryCompactMap(),
            this.testTryMap(),
            this.testFlatMap(),
            this.testDelay(),
            this.testFilter(),
            this.testTryFilter(),
            this.testReplaceEmpty(),
            this.testReplaceError(),
            this.testRemoveDuplicates(),
            this.testTryRemoveDuplicates(),
            this.testTryReduce(),
            this.testIgnoreOutput(),
            this.testCollectByCount(),
            this.testCollectByTime(),
            this.testCollectByTimeOrCount(),
            this.testMax(),
            this.testMin(),
            this.testTryMin(),
            this.testTryMax(),
            this.testCount(),
            this.testContains(),
            this.testContainsWhere(),
            this.testAllSatisfy(),
            this.testCombineLatest(),
            this.testZip(),
            this.testSwitchToLatest(),
            this.testPrint(),
            this.testHandleEvents(),
            this.testCatch(),
            this.testPrefixUntil(),
            this.testDropUntil()
        };

//        tests = new IPublisher[] {
//            testMeasureInterval(),
//            testRecord()
//        };

        final IPublisher[] ftests = tests;

        Publisher.merge(tests).sink(new Sink() {

            protected void onValue(Object value) {
                TestCase.Result result = (TestCase.Result) value;
                String message = result.getMessage();
                S.println(message);
                run++;
                if (result.passed()) {
                    passed++;
                } else {
                    failed++;
                }
                form.append(message + "\n");
            }

            protected void onCompletion(Completion completion) {
                String result = new StringBuffer("\nTOTAL: ").append(ftests.length).append(" RUN: ").append(run).append("\nFAILED: ").append(failed).append(" PASSED: ").append(passed).append("\n").append((passed * 1000 * 100) / (ftests.length * 1000)).append("% OK.").toString();

                S.println(result);
                form.append(result);
            }
        });
    }

    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
    }

    protected void pauseApp() {
    }

    private IPublisher testCancel() {
        return new TestCase("CANCEL") {

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
                            pass();
                        } else {
                            fail();
                        }
                    }
                });

                assertThread.start();
            }
        };
    }

    private IPublisher testJust() {
        return new TestCase("JUST") {

            protected void test() {
                (new Just("Hello COMBIME!")).sink(new Sink() {

                    protected void onValue(Object value) {
                        if (value.equals("Hello COMBIME!")) {
                            pass();
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
    }

    private IPublisher testMap() {
        return new TestCase("TEST RAW MAP") {

            String result = "";

            protected void test() {
                Map m = new Map() {

                    public Object mapValue(Object value) {
                        return ((String) value).toUpperCase();
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
                        result = (String) value;
                    }

                    protected void onCompletion(Completion completion) {
                        assertEqual("!AA!", result);
                        pass();
                    }
                });
                m.receiveInput("a");
                m.receiveCompletion(new Completion(true));
            }
        };
    }

    private IPublisher testFutureMap() {
        return new TestCase("FUTURE+MAP") {

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
                        assertEqual("1hello2", value);
                        pass();
                    }
                });
            }
        };
    }

    private IPublisher testEmpty() {
        return new TestCase("EMPTY") {

            protected void test() {
                (new Empty()).sink(new Sink() {

                    protected void onValue(Object value) {
                        fail();
                    }

                    protected void onCompletion(Completion completion) {
                        if (completion.isSuccess()) {
                            pass();
                        } else {
                            fail();
                        }
                    }
                });
            }
        };
    }

    private IPublisher testFail() {
        return new TestCase("FAIL") {

            protected void test() {
                (new Fail()).sink(new Sink() {

                    protected void onValue(Object value) {
                        fail();
                    }

                    protected void onCompletion(Completion completion) {
                        if (completion.isSuccess()) {
                            fail();
                        } else {
                            pass();
                        }
                    }
                });
            }
        };
    }

    private IPublisher testReduce() {
        return new TestCase("REDUCE") {

            private String result = "";

            protected void test() {
                result = "";
                ISubject subj = new PassthroughSubject();
                subj.to(new Reduce("") {

                    protected Object reduce(Object subresult, Object currentValue) {
                        return (String) subresult + currentValue;
                    }
                }).sink(new Sink() {

                    protected void onValue(Object value) {
                        result += (String) value;
                    }

                    protected void onCompletion(Completion completion) {
                        assertEqual("123", result);
                        pass();
                    }
                });

                subj.sendValue("1");
                subj.sendValue("2");
                subj.sendValue("3");
                subj.sendCompletion(new Completion(true));
            }
        };
    }

    private IPublisher testScan() {
        return new TestCase("SCAN") {

            private String result = "";

            protected void test() {
                result = "";
                ISubject subj = new PassthroughSubject();
                subj.to(new Scan("") {

                    protected Object scan(Object subresult, Object currentValue) {
                        return (String) subresult + currentValue;
                    }
                }).sink(new Sink() {

                    protected void onValue(Object value) {
                        result += (String) value;
                    }

                    protected void onCompletion(Completion completion) {
                        assertEqual("112123", result);
                        pass();
                    }
                });

                subj.sendValue("1");
                subj.sendValue("2");
                subj.sendValue("3");
                subj.sendCompletion(new Completion(true));
            }
        };
    }

    private IPublisher testMergeReduce() {
        return new TestCase("MERGE+REDUCE") {

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
                }).sink(new Sink() {

                    protected void onValue(Object value) {
                        result += (String) value;
                    }

                    protected void onCompletion(Completion completion) {
                        assertEqual("123", result);
                        pass();
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
    }

    private IPublisher testSequence() {
        return new TestCase("SEQUENCE") {

            String res = "";

            protected void test() {
                Sequence s = new Sequence(new String[]{"1", "2", "3"});
                s.sink(
                        new Subscriber() {

                            public void receiveSubscription(ISubscription subscription) {
                                super.receiveSubscription(subscription);
                                subscription.requestValues(new Demand(1));
                            }

                            public Demand receiveInput(Object input) {
                                onValue(input);
                                return new Demand(1);
                            }

                            protected void onValue(Object value) {
                                res += value;
                            }

                            protected void onCompletion(Completion completion) {
                                assertEqual(res, "123");
                                pass();
                            }
                        });
            }
        };
    }

    private IPublisher testCompactMap() {
        return new TestCase("COMPACT MAP") {

            String result = "";

            protected void test() {
                (new Sequence(new Object[]{"1", null, "2", null, "3"}))
                        .to(new CompactMap())
                        .sink(new Sink() {
                            protected void onValue(Object value) {
                                result += value;
                            }
                            protected void onCompletion(Completion completion) {
                                assertEqual("123", result);
                                pass();
                            }
                });
            }
        };
    }

    private IPublisher testTryCompactMap() {
        return new TestCase("TRY COMPACT MAP") {

            String result = "";

            protected void test() {
                (new Sequence(new Object[]{"1", null, "2", null, "3"})).to(new CompactMap() {

                    {
                        isTry = true;
                    }

                    public Object mapValue(Object value) {
                        if (value.equals("2")) {
                            int i = 1 / 0;
                        }
                        return super.mapValue(value);
                    }
                }).sink(new Sink() {

                    protected void onValue(Object value) {
                        result += value;
                    }

                    protected void onCompletion(Completion completion) {
                        if (completion.isSuccess()) {
                            fail();
                            return;
                        }
                        assertEqual("1", result);
                        pass();
                    }
                });
            }
        };
    }

    private IPublisher testTryMap() {
        return new TestCase("TRY MAP") {

            protected void test() {
                (new Just("0")).to(new Map() {

                    {
                        isTry = true;
                    }

                    public Object mapValue(Object value) {
                        return ((Object[]) value)[0];
                    }
                }).sink(new Sink() {

                    protected void onValue(Object value) {
                        fail();
                    }

                    protected void onCompletion(Completion completion) {
                        if (completion.isSuccess() || completion.getFailure() == null) {
                            fail();
                            return;
                        }
                        pass();
                    }
                });
            }
        };
    }

    private IPublisher testFlatMap() {
        return new TestCase("FLAT MAP") {

            String result = "";
            int j = 0;

            protected void test() {
                new Sequence(new Integer[]{new Integer(1), new Integer(2), new Integer(3)}).to(new FlatMap() {

                    protected IPublisher flatMap(Object value) {
                        int i = ((Integer) value).intValue();
                        if (i % 2 == 0) {
                            return new Just(i + "" + i).to(new Delay(1000 + i * 100));
                        }
                        return new Just("" + i).to(new Delay(1000 + i * 100));
                    }
                }).sink(new Sink() {
                    protected void onValue(Object value) {
                        result += value;
                        j++;
                        if (j == 3) {
                            assertEqual("1223", result);
                            pass();
                        }
                    }
                });
            }
        };
    }

    private IPublisher testDelay() {
        return new TestCase("DELAY") {

            String result = "";

            protected void test() {
                final ICancellable canc = (new Just("123")).to(new Delay(1000)).sink(new Sink() {

                    protected void onValue(Object value) {
                        result = (String) value;
                    }
                });

                (new Thread(new Runnable() {

                    public void run() {
                        S.sleep(500);
                        if (!result.equals("")) {
                            fail();
                            canc.cancel();
                        }
                    }
                })).start();

                (new Thread(new Runnable() {

                    public void run() {
                        S.sleep(1100);
                        assertEqual("123", result);
                        pass();
                    }
                })).start();
            }
        };
    }

    private IPublisher testTryFilter() {
        return new TestCase("TRY FILTER") {

            String result = "";

            protected void test() {
                new Sequence(S.boxed(new int[]{1, 2, 3, 4, 5, 6})).to(new Filter() {

                    {
                        isTry = true;
                    }

                    protected boolean shouldKeep(Object input) {
                        int i = ((Integer) input).intValue();
                        if (i == 4) {
                            int j = i / 0;
                        }
                        return i > 0;
                    }
                }).sink(new Sink() {

                    protected void onValue(Object value) {
                        result += ((Integer) value).intValue();
                    }

                    protected void onCompletion(Completion completion) {
                        assertEqual("123", result);
                        pass();
                    }
                });
            }
        };
    }

    private IPublisher testFilter() {
        return new TestCase("FILTER") {

            String result = "";

            protected void test() {
                new Sequence(S.boxed(new int[]{1, 2, 3, 4, 5, 6})).to(new Filter() {

                    protected boolean shouldKeep(Object input) {
                        Integer i = (Integer) input;
                        return i.intValue() % 2 == 0;
                    }
                }).sink(new Sink() {

                    protected void onValue(Object value) {
                        result += ((Integer) value).intValue();
                    }

                    protected void onCompletion(Completion completion) {
                        assertEqual("246", result);
                        pass();
                    }
                });
            }
        };
    }

    private IPublisher testReplaceEmpty() {
        return new TestCase("REPLACE EMPTY") {

            String result = "";

            protected void test() {
                (new Empty()).to(new ReplaceEmpty("123")).sink(new Sink() {

                    protected void onValue(Object value) {
                        result = (String) value;
                    }

                    protected void onCompletion(Completion completion) {
                        assertEqual("123", result);
                        pass();
                    }
                });
            }
        };
    }

    private IPublisher testReplaceError() {
        return new TestCase("REPLACE ERROR") {

            String result = "";

            protected void test() {
                (new Just("0")).to(new Map() {

                    {
                        isTry = true;
                    }

                    public Object mapValue(Object value) {
                        int i = 1 / 0;
                        return value;
                    }
                }).to(new ReplaceError("123")).sink(new Sink() {

                    protected void onValue(Object value) {
                        result = (String) value;
                        assertEqual("123", result);
                        pass();
                    }
                });
            }
        };
    }

    private IPublisher testRemoveDuplicates() {
        return new TestCase("REMOVE DUPLICATES") {

            String result = "";

            protected void test() {
                (new Sequence(new String[]{"1", "1", "2", null, null, null, "3", "3", "3", "hello", "hello"})).to(new RemoveDuplicates() {

                    protected boolean areEqual(Object object1, Object object2) {
                        String s1 = (String) object1;
                        String s2 = (String) object1;
                        if (s1 != null && s2 != null && s1.length() > 1 && s2.length() > 1) {
                            return false;
                        }
                        return super.areEqual(object1, object2);
                    }
                }).sink(new Sink() {

                    protected void onValue(Object value) {
                        result += value;
                    }

                    protected void onCompletion(Completion completion) {
                        assertEqual("12null3hellohello", result);
                        pass();
                    }
                });
            }
        };
    }

    private IPublisher testTryRemoveDuplicates() {
        return new TestCase("TRY REMOVE DUPLICATES") {

            String result = "";

            protected void test() {
                (new Sequence(new String[]{"1", "1", "2", null, null, null, "3", "3", "3", "hello", "hello"})).to(new RemoveDuplicates() {

                    {
                        isTry = true;
                    }

                    protected boolean areEqual(Object object1, Object object2) {
                        String s1 = (String) object1;
                        String s2 = (String) object1;
                        if (s1 != null && s2 != null && s1.length() > 1 && s2.length() > 1) {
                            return false;
                        }

                        if (result.equals("12")) {
                            int i = 1 / 0;
                        }

                        return super.areEqual(object1, object2);
                    }
                }).sink(new Sink() {

                    protected void onValue(Object value) {
                        result += value;
                    }

                    protected void onCompletion(Completion completion) {
                        if (completion.isSuccess()) {
                            fail();
                            return;
                        }
                        assertEqual("12", result);
                        pass();
                    }
                });
            }
        };
    }

    private IPublisher testTryReduce() {
        return new TestCase("TRY REDUCE") {

            private String result = "";

            protected void test() {
                result = "";
                ISubject subj = new PassthroughSubject();
                subj.to(new Reduce("") {

                    {
                        isTry = true;
                    }

                    protected Object reduce(Object subresult, Object currentValue) {
                        if (currentValue.equals("3")) {
                            int i = 1 / 0;
                        }
                        return (String) subresult + currentValue;
                    }
                }).sink(new Sink() {

                    protected void onValue(Object value) {
                        result += (String) value;
                    }

                    protected void onCompletion(Completion completion) {
                        assertEqual("", result);
                        assertEqual(completion.isSuccess(), false);
                        pass();
                    }
                });

                subj.sendValue("1");
                subj.sendValue("2");
                subj.sendValue("3");
                subj.sendCompletion(new Completion(true));
            }
        };
    }

    private IPublisher testIgnoreOutput() {
        return new TestCase("IGNORE OUTPUT") {

            protected void test() {
                (new Sequence(new String[]{"1", "2", "3", "4", "5"})).to(new IgnoreOutput()).sink(new Sink() {

                    protected void onValue(Object value) {
                        fail();
                    }

                    protected void onCompletion(Completion completion) {
                        assertEqual(true, completion.isSuccess());
                        assertEqual(null, completion.getFailure());
                        pass();
                    }
                });
            }
        };
    }

    private IPublisher testCollectByCount() {
        return new TestCase("COLLECT BY COUNT") {

            int i = 0;

            protected void test() {
                (new Sequence(new String[]{"1", "2", "3", "4", "5", "6", "7"})).to(new Collect((int) 3)).sink(new Sink() {

                    protected void onValue(Object value) {
                        if (i == 0) {
                            assertEqual(true, S.arraysEqual(new Object[]{"1", "2", "3"}, (Object[]) value));
                        } else if (i == 1) {
                            assertEqual(true, S.arraysEqual(new Object[]{"4", "5", "6"}, (Object[]) value));
                        } else if (i == 2) {
                            assertEqual(true, S.arraysEqual(new Object[]{"7"}, (Object[]) value));
                        } else {
                            fail();
                        }
                        i++;
                    }

                    protected void onCompletion(Completion completion) {
                        assertEqual(true, completion.isSuccess());
                        assertEqual(null, completion.getFailure());
                        pass();
                    }
                });
            }
        };
    }

    private IPublisher testCollectByTime() {
        return new TestCase("COLLECT BY TIME") {

            int i = 0;
            String result = "";
            Thread t1;
            Thread t2;
            ISubject pts = new PassthroughSubject();

            protected void test() {
                pts.to(new Collect((long) 300)).sink(new Sink() {

                    protected void onValue(Object value) {
                        result += S.arrayToString((Object[]) value) + "+";
                    }
                });

                t1 = new Thread(new Runnable() {

                    public void run() {
                        pts.sendValue("1");
                        S.sleep(101);
                        pts.sendValue("2");
                        S.sleep(101);
                        pts.sendValue("3");
                        S.sleep(101);
                        pts.sendValue("4");
                        S.sleep(101);
                        pts.sendValue("5");
                        S.sleep(101);
                        pts.sendValue("6");
                        S.sleep(101);
                        pts.sendValue("7");
                        S.sleep(101);
                        pts.sendCompletion(new Completion(true));
                    }
                });
                t1.start();

                new S.Delay(1000) {

                    protected void work() {
                        assertEqual("123+456+7+", result);
                        pass();
                    }
                };
            }
        };
    }

    private IPublisher testCollectByTimeOrCount() {
        return new TestCase("COLLECT BY TIME OR COUNT") {

            int i = 0;
            String result = "";
            Thread t1;
            Thread t2;
            ISubject pts = new PassthroughSubject();

            protected void test() {
                pts.to(new Collect((long) 300, 4)).sink(new Sink() {

                    protected void onValue(Object value) {
                        result += S.arrayToString((Object[]) value) + "+";
                    }
                });

                t1 = new Thread(new Runnable() {

                    public void run() {
                        pts.sendValue("1");
                        S.sleep(101);
                        pts.sendValue("2");
                        S.sleep(101);
                        pts.sendValue("3");
                        S.sleep(101);
                        pts.sendValue("4");
                        S.sleep(10);
                        pts.sendValue("5");
                        S.sleep(10);
                        pts.sendValue("6");
                        S.sleep(10);
                        pts.sendValue("7");
                        S.sleep(10);
                        pts.sendValue("8");
                        S.sleep(10);
                        pts.sendValue("9");
                        S.sleep(10);
                        pts.sendCompletion(new Completion(true));
                    }
                });
                t1.start();

                new S.Delay(1000) {

                    protected void work() {
                        assertEqual("123+4567+89+", result);
                        pass();
                    }
                };
            }
        };
    }

    private IPublisher testMin() {
        return new TestCase("MIN") {

            protected void test() {
                (new Sequence(S.boxed(new int[]{5, 2, 4, 1, 3}))).to(new Min() {

                    protected boolean isNewValueLess(Object currentMin, Object newValue) {
                        int c = ((Integer) currentMin).intValue();
                        int n = ((Integer) newValue).intValue();

                        return n < c;
                    }
                }).sink(new Sink() {

                    protected void onValue(Object value) {
                        assertEqual(new Integer(1), value);
                    }

                    protected void onCompletion(Completion completion) {
                        assertEqual(true, completion.isSuccess());
                        assertEqual(null, completion.getFailure());
                        pass();
                    }
                });
            }
        };
    }

    private IPublisher testMax() {
        return new TestCase("MAX") {

            protected void test() {
                (new Sequence(S.boxed(new int[]{4, 2, 5, 1, 3}))).to(new Max() {

                    protected boolean isNewValueGreater(Object currentMax, Object newValue) {
                        int c = ((Integer) currentMax).intValue();
                        int n = ((Integer) newValue).intValue();
                        return n > c;
                    }
                }).sink(new Sink() {

                    protected void onValue(Object value) {
                        assertEqual(new Integer(5), value);
                    }

                    protected void onCompletion(Completion completion) {
                        assertEqual(true, completion.isSuccess());
                        assertEqual(null, completion.getFailure());
                        pass();
                    }
                });
            }
        };
    }

    private IPublisher testTryMin() {
        return new TestCase("TRY MIN") {

            protected void test() {
                (new Sequence(S.boxed(new int[]{5, 2, 4, 1, 3}))).to(new Min() {

                    {
                        isTry = true;
                    }

                    protected boolean isNewValueLess(Object currentMin, Object newValue) {
                        int c = ((Integer) currentMin).intValue();
                        int n = ((Integer) newValue).intValue();

                        if (n == 1) {
                            n = 1 / 0;
                        }

                        return n < c;
                    }
                }).sink(new Sink() {

                    protected void onValue(Object value) {
                        fail();
                    }

                    protected void onCompletion(Completion completion) {
                        assertEqual(false, completion.isSuccess());
                        assertEqual(false, completion.getFailure() == null);
                        pass();
                    }
                });
            }
        };
    }

    private IPublisher testTryMax() {
        return new TestCase("TRY MAX") {

            protected void test() {
                (new Sequence(S.boxed(new int[]{4, 2, 5, 1, 3}))).to(new Max() {

                    {
                        isTry = true;
                    }

                    protected boolean isNewValueGreater(Object currentMax, Object newValue) {
                        int c = ((Integer) currentMax).intValue();
                        int n = ((Integer) newValue).intValue();

                        if (n == 1) {
                            n = 1 / 0;
                        }

                        return n > c;
                    }
                }).sink(new Sink() {

                    protected void onValue(Object value) {
                        fail();
                    }

                    protected void onCompletion(Completion completion) {
                        assertEqual(false, completion.isSuccess());
                        assertEqual(false, completion.getFailure() == null);
                        pass();
                    }
                });
            }
        };
    }

    private IPublisher testCount() {
        return new TestCase("COUNT") {

            protected void test() {
                (new Sequence(S.boxed(new int[]{4, 2, 5, 1, 0, 3}))).to(new Count()).sink(new Sink() {

                    protected void onValue(Object value) {
                        assertEqual(new Integer(6), value);
                    }

                    protected void onCompletion(Completion completion) {
                        assertEqual(true, completion.isSuccess());
                        assertEqual(null, completion.getFailure());
                        pass();
                    }
                });
            }
        };
    }

    private IPublisher testContains() {
        return new TestCase("CONTAINS") {

            int count = 0;

            protected void test() {
                (new Sequence(S.boxed(new int[]{4, 2, 5, 1, 0, 3}))).to(new Contains(new Integer(1))).sink(new Sink() {

                    protected void onValue(Object value) {
                        count++;
                        assertEqual(true, ((Boolean) value).booleanValue());
                    }

                    protected void onCompletion(Completion completion) {
                        assertEqual(true, completion.isSuccess());
                        assertEqual(null, completion.getFailure());
                        assertEqual(1, count);
                        pass();
                    }
                });
            }
        };
    }

    private IPublisher testContainsWhere() {
        return new TestCase("CONTAINS WHERE") {

            int count = 0;

            protected void test() {
                (new Sequence(S.boxed(new int[]{4, 2, 5, 1, 0, -4, 3}))).to(new ContainsWhere() {

                    protected boolean doesSatisfy(Object obj) {
                        return ((Integer) obj).intValue() < 0;
                    }
                }).sink(new Sink() {

                    protected void onValue(Object value) {
                        count++;
                        assertEqual(true, ((Boolean) value).booleanValue());
                    }

                    protected void onCompletion(Completion completion) {
                        assertEqual(true, completion.isSuccess());
                        assertEqual(null, completion.getFailure());
                        assertEqual(1, count);
                        pass();
                    }
                });
            }
        };
    }

    private IPublisher testAllSatisfy() {
        return new TestCase("ALL SATISFY") {

            int count = 0;

            protected void test() {
                (new Sequence(S.boxed(new int[]{4, 2, 5, -1, 0, 3}))).to(new AllSatisfy() {

                    protected boolean doesSatisfy(Object obj) {
                        return ((Integer) obj).intValue() >= 0;
                    }
                }).sink(new Sink() {

                    protected void onValue(Object value) {
                        count++;
                        assertEqual(false, ((Boolean) value).booleanValue());
                    }

                    protected void onCompletion(Completion completion) {
                        assertEqual(true, completion.isSuccess());
                        assertEqual(null, completion.getFailure());
                        assertEqual(1, count);
                        pass();
                    }
                });
            }
        };
    }

    private IPublisher testCombineLatest() {
        return new TestCase("COMBINE LATEST") {

            Object[] test = new Object[]{
                new Object[]{"1", null, null},
                new Object[]{"1", "2", null},
                new Object[]{"1", "2", "3"},
                new Object[]{"1", "2", "4"},
                new Object[]{"1", "5", "4"},
                new Object[]{"6", "5", "4"},};
            int i = 0;
            Thread t;

            protected void test() {
                final ISubject pub1 = new PassthroughSubject();
                final ISubject pub2 = new PassthroughSubject();
                final ISubject pub3 = new PassthroughSubject();

                (new CombineLatest(new IPublisher[]{pub1, pub2, pub3})).sink(new Sink() {

                    protected void onValue(Object value) {
                        Object[] ti = (Object[]) test[i];
                        assertEqual(true, S.arraysEqual((Object[]) value, ti));
                        i++;
                    }

                    protected void onCompletion(Completion completion) {
                        assertEqual(6, i);
                        assertEqual(true, completion.isSuccess());
                        assertEqual(null, completion.getFailure());
                        pass();
                    }
                });

                t = new Thread(new Runnable() {

                    public void run() {
                        S.sleep(100);
                        pub1.sendValue("1");
                        S.sleep(140);
                        pub2.sendValue("2");
                        S.sleep(20);
                        pub3.sendValue("3");
                        S.sleep(30);
                        pub3.sendValue("4");
                        S.sleep(90);
                        pub2.sendValue("5");
                        S.sleep(150);
                        pub1.sendValue("6");
                        S.sleep(100);

                        pub2.sendCompletion(new Completion(true));
                        pub1.sendCompletion(new Completion(true));
                        pub3.sendCompletion(new Completion(true));
                    }
                });

                t.start();
            }
        };
    }

    private IPublisher testZip() {
        return new TestCase("ZIP") {

            Object[] test = new Object[]{
                new Object[]{"1", "2", "3"},
                new Object[]{"6", "5", "4"},};
            int i = 0;
            Thread t;

            protected void test() {
                final ISubject pub1 = new PassthroughSubject();
                final ISubject pub2 = new PassthroughSubject();
                final ISubject pub3 = new PassthroughSubject();

                i = 0;

                (new Zip(new IPublisher[]{pub1, pub2, pub3})).sink(new Sink() {

                    protected void onValue(Object value) {
                        Object[] ti = (Object[]) test[i];
                        assertEqual(true, S.arraysEqual((Object[]) value, ti));
                        i++;
                    }

                    protected void onCompletion(Completion completion) {
                        assertEqual(2, i);
                        assertEqual(true, completion.isSuccess());
                        assertEqual(null, completion.getFailure());
                        pass();
                    }
                });

                t = new Thread(new Runnable() {

                    public void run() {
                        S.sleep(100);
                        pub1.sendValue("1");
                        S.sleep(140);
                        pub2.sendValue("2");
                        S.sleep(20);
                        pub3.sendValue("3");
                        S.sleep(30);
                        pub3.sendValue("4");
                        S.sleep(90);
                        pub2.sendValue("5");
                        S.sleep(150);
                        pub1.sendValue("6");
                        S.sleep(100);
                        pub2.sendValue("7");
                        S.sleep(150);
                        pub1.sendValue("8");
                        S.sleep(100);

                        pub2.sendCompletion(new Completion(true));
                        pub1.sendCompletion(new Completion(true));
                        pub3.sendCompletion(new Completion(true));
                    }
                });

                t.start();
            }
        };
    }

    private IPublisher testSwitchToLatest() {
        return new TestCase("SWITCH TO LATEST") {

            String result = "";

            protected void test() {
                ISubject pub1 = new PassthroughSubject();
                ISubject pub2 = new PassthroughSubject();
                ISubject pub3 = new PassthroughSubject();

                ISubject supersub = new PassthroughSubject();
                supersub.to(new SwitchToLatest()).sink(new Sink() {

                    protected void onValue(Object value) {
                        result += (String) value;
                    }

                    protected void onCompletion(Completion completion) {
                        assertEqual(true, completion.isSuccess());
                        assertEqual(null, completion.getFailure());
                        assertEqual("113", result);
                        pass();
                    }
                });

                supersub.sendValue(pub1);

                pub1.sendValue("1");
                pub2.sendValue("2");
                pub3.sendValue("3");

                pub1.sendValue("1");
                pub2.sendValue("2");
                supersub.sendValue(pub2);
                pub3.sendValue("3");
                pub1.sendValue("1");
                supersub.sendValue(pub3);
                pub2.sendValue("2");
                pub3.sendValue("3");

                pub2.sendCompletion(new Completion(true));
                pub1.sendCompletion(new Completion(true));
                pub3.sendCompletion(new Completion(true));
            }
        };
    }

    private IPublisher testPrint() {
        return new TestCase("PRINT") {

            protected void test() {
                PassthroughSubject pub = new PassthroughSubject();
                pub.print(new Print("DEBUG: ", null)).sink(new Sink() {
                });

                pub.sendValue("1");
                pub.sendCompletion(new Completion(true));

                pub = new PassthroughSubject();
                ICancellable canc = pub.print(new Print("DEBUG: ", null)).sink(new Sink() {
                });

                pub.sendValue("2");
                canc.cancel();
                pub.sendCompletion(new Completion(true));

                pass();
            }
        };
    }

    private IPublisher testHandleEvents() {
        return new TestCase("HANDLE EVENTS") {

            private String result = "";

            protected void test() {
                PassthroughSubject pub = new PassthroughSubject();
                pub.handleEvents(new HandleEvents() {

                    public void receiveSubscription(ISubscription subscription) {
                        result += 1;
                    }

                    public void receiveDemand(ISubscription subscription, Demand demand) {
                        result += 2;
                    }

                    public void receiveOutput(ISubscription subscription, Object value) {
                        result += value;
                    }

                    public void receiveCompletion(ISubscription subscription, Completion completion) {
                        result += 3;
                    }

                    public void receiveCancel(ISubscription subscription) {
                        fail("RECEIVED CANCEL ON A COMPLETED SEQUENCE!");
                    }
                }).sink(new Sink() {
                });

                pub.sendValue("A");
                pub.sendCompletion(new Completion(true));

                PassthroughSubject pub2 = new PassthroughSubject();
                ICancellable canc = pub2.handleEvents(new HandleEvents() {

                    public void receiveSubscription(ISubscription subscription) {
                        result += 1;
                    }

                    public void receiveDemand(ISubscription subscription, Demand demand) {
                        result += 2;
                    }

                    public void receiveOutput(ISubscription subscription, Object value) {
                        result += value;
                    }

                    public void receiveCompletion(ISubscription subscription, Completion completion) {
                        fail("RECEIVED COMPLETION ON A CANCELLED SEQUENCE!");
                    }

                    public void receiveCancel(ISubscription subscription) {
                        result += 4;
                    }
                }).sink(new Sink() {
                });

                pub2.sendValue("B");
                canc.cancel();
                pub2.sendCompletion(new Completion(true));

                assertEqual("12A312B4", result);
                pass();
            }
        };
    }

    private IPublisher testDrop() {
        return new TestCase("DROP") {

            private String result = "";

            protected void test() {
                ISubject sub = new PassthroughSubject();
                sub.to(new Drop(2)).sink(new Sink() {

                    protected void onValue(Object value) {
                        result += value;
                    }

                    protected void onCompletion(Completion completion) {
                        assertEqual("3", result);
                        assertEqual(true, completion.isSuccess());
                        assertEqual(null, completion.getFailure());
                        pass();
                    }
                });

                sub.sendValue("1");
                sub.sendValue("2");
                sub.sendValue("3");
                sub.sendCompletion(new Completion(true));
            }
        };
    }

    private IPublisher testFirst() {
        return new TestCase("FIRST") {

            private String result = "";

            protected void test() {
                ISubject sub = new PassthroughSubject();
                sub.to(new First() {

                    protected boolean where(Object object) {
                        return ((String) object).length() > 2;
                    }
                }).sink(new Sink() {

                    protected void onValue(Object value) {
                        result += value;
                    }

                    protected void onCompletion(Completion completion) {
                        assertEqual("333", result);
                        assertEqual(true, completion.isSuccess());
                        assertEqual(null, completion.getFailure());
                        pass();
                    }
                });

                sub.sendValue("1");
                sub.sendValue("22");
                sub.sendValue("333");
                sub.sendValue("4444");
                sub.sendCompletion(new Completion(true));
            }
        };
    }

    private IPublisher testOutput() {
        return new TestCase("OUTPUT") {

            private Object[] result;

            protected void test() {
                ISubject sub = new PassthroughSubject();
                sub.to(new Output(1, 3)).sink(new Sink() {

                    protected void onValue(Object value) {
                        result = (Object[]) value;
                    }

                    protected void onCompletion(Completion completion) {
                        assertEqual(new Object[]{"2", "3", "4"}, result);
                        assertEqual(true, completion.isSuccess());
                        assertEqual(null, completion.getFailure());
                    }
                });

                sub.sendValue("1");
                sub.sendValue("2");
                sub.sendValue("3");
                sub.sendValue("4");
                sub.sendValue("5");
                sub.sendCompletion(new Completion(true));

                sub = new PassthroughSubject();
                sub.to(new Output(3)).sink(new Sink() {

                    protected void onValue(Object value) {
                        result = (Object[]) value;
                    }

                    protected void onCompletion(Completion completion) {
                        assertEqual(new Object[]{"4"}, result);
                        assertEqual(true, completion.isSuccess());
                        assertEqual(null, completion.getFailure());
                        pass();
                    }
                });

                sub.sendValue("1");
                sub.sendValue("2");
                sub.sendValue("3");
                sub.sendValue("4");
                sub.sendValue("5");
                sub.sendCompletion(new Completion(true));
            }
        };
    }

    private IPublisher testPrefix() {
        return new TestCase("PREFIX") {

            private String result = "";

            protected void test() {
                ISubject sub = new PassthroughSubject();
                sub.to(new Prefix(3)).sink(new Sink() {

                    protected void onValue(Object value) {
                        result += value;
                    }

                    protected void onCompletion(Completion completion) {
                        assertEqual("123", result);
                        assertEqual(true, completion.isSuccess());
                        assertEqual(null, completion.getFailure());
                        pass();
                    }
                });

                sub.sendValue("1");
                sub.sendValue("2");
                sub.sendValue("3");
                sub.sendValue("4");
                sub.sendValue("5");
                sub.sendCompletion(new Completion(true));
            }
        };
    }

    private IPublisher testPrepend() {
        return new TestCase("PREPEND") {

            private String result = "";

            protected void test() {
                ISubject sub1 = new PassthroughSubject();
                ISubject sub2 = new PassthroughSubject();

                sub1.to(new Prepend(sub2)).sink(new Sink() {

                    protected void onValue(Object value) {
                        result += value;
                    }

                    protected void onCompletion(Completion completion) {
                        assertEqual("12345", result);
                        assertEqual(true, completion.isSuccess());
                        assertEqual(null, completion.getFailure());
                        pass();
                    }
                });

                sub2.sendValue("1");
                sub2.sendValue("2");
                sub1.sendValue("3");
                sub1.sendValue("4");
                sub1.sendValue("5");
                sub1.sendCompletion(new Completion(true));
                sub2.sendCompletion(new Completion(true));
            }
        };
    }

    private IPublisher testTimeout() {
        return new TestCase("TIMEOUT") {

            protected void test() {
                new Thread(new Runnable() {

                    public void run() {
                        ISubject sub1 = new PassthroughSubject();
                        ISubject sub2 = new PassthroughSubject();

                        sub1.to(new Timeout(200)).sink(new Sink() {

                            private String result = "";

                            protected void onValue(Object value) {
                                result += value;
                            }

                            protected void onCompletion(Completion completion) {
                                assertEqual("123", result);
                                assertEqual(true, completion.isSuccess());
                                assertEqual(null, completion.getFailure());
                            }
                        });

                        sub1.sendValue("1");
                        S.sleep(100);
                        sub1.sendValue("2");
                        S.sleep(50);
                        sub1.sendValue("3");
                        S.sleep(150);

                        sub1.sendCompletion(new Completion(true));

                        sub2.to(new Timeout(200)).sink(new Sink() {

                            private String result = "";

                            protected void onValue(Object value) {
                                result += value;
                            }

                            protected void onCompletion(Completion completion) {
                                assertEqual("12", result);
                                assertEqual(false, completion.isSuccess());
                                assertEqual(true, null != completion.getFailure());
                                pass();
                            }
                        });

                        sub2.sendValue("1");
                        S.sleep(100);
                        sub2.sendValue("2");
                        S.sleep(500);
                        sub2.sendValue("3");
                        S.sleep(200);

                        sub2.sendCompletion(new Completion(true));
                    }
                }).start();
            }
        };
    }

    private IPublisher testDebounce() {
        return new TestCase("DEBOUNCE") {

            protected void test() {
                new Thread(new Runnable() {

                    public void run() {
                        ISubject sub1 = new PassthroughSubject();

                        sub1.to(new Debounce(300)).sink(new Sink() {

                            private String result = "";

                            protected void onValue(Object value) {
                                result += value;
                            }

                            protected void onCompletion(Completion completion) {
                                assertEqual("1234", result);
                                assertEqual(true, completion.isSuccess());
                                assertEqual(null, completion.getFailure());
                                pass();
                            }
                        });

                        sub1.sendValue("3");
                        S.sleep(100);
                        sub1.sendValue("4");
                        S.sleep(100);
                        sub1.sendValue("1");
                        S.sleep(300);

                        sub1.sendValue("8");
                        S.sleep(100);
                        sub1.sendValue("0");
                        S.sleep(100);
                        sub1.sendValue("2");
                        S.sleep(300);


                        sub1.sendValue("1");
                        S.sleep(100);
                        sub1.sendValue("1");
                        sub1.sendValue("1");
                        S.sleep(100);
                        sub1.sendValue("3");
                        S.sleep(300);

                        sub1.sendValue("4");
                        S.sleep(100);

                        sub1.sendCompletion(new Completion(true));
                    }
                }).start();
            }
        };
    }

    private IPublisher testThrottle() {
        return new TestCase("THROTTLE") {

            protected void test() {
                new Thread(new Runnable() {

                    public void run() {
                        ISubject sub1 = new PassthroughSubject();
                        sub1.to(new Throttle(300)).sink(new Sink() {

                            private String result = "";

                            protected void onValue(Object value) {
                                result += value;
                            }

                            protected void onCompletion(Completion completion) {
                                assertEqual("357", result);
                                assertEqual(true, completion.isSuccess());
                                assertEqual(null, completion.getFailure());
                            }
                        });

                        sub1.sendValue("1");
                        S.sleep(120);
                        sub1.sendValue("2");
                        S.sleep(120);
                        sub1.sendValue("3");
                        S.sleep(120);

                        sub1.sendValue("4");
                        S.sleep(120);
                        sub1.sendValue("5");
                        S.sleep(200);
                        sub1.sendValue("6");
                        S.sleep(120);

                        sub1.sendValue("7");

                        sub1.sendCompletion(new Completion(true));

                        PassthroughSubject sub2 = new PassthroughSubject();
                        sub2.to(new Throttle(300, false)).sink(new Sink() {

                            private String result = "";

                            protected void onValue(Object value) {
                                result += value;
                            }

                            protected void onCompletion(Completion completion) {
                                assertEqual("146", result);
                                assertEqual(true, completion.isSuccess());
                                assertEqual(null, completion.getFailure());
                                pass();
                            }
                        });

                        sub2.sendValue("1");
                        S.sleep(120);
                        sub2.sendValue("2");
                        S.sleep(120);
                        sub2.sendValue("3");
                        S.sleep(120);
                        sub2.sendValue("4");
                        S.sleep(120);
                        sub2.sendValue("5");
                        S.sleep(200);
                        sub2.sendValue("6");
                        S.sleep(120);
                        sub2.sendValue("7");

                        sub2.sendCompletion(new Completion(true));

                        sub2.sendValue("a");
                        S.sleep(120);
                        sub2.sendValue("b");
                        S.sleep(200);
                        sub2.sendValue("c");
                        S.sleep(120);
                    }
                }).start();
            }
        };
    }

    private IPublisher testMeasureInterval() {
        return new TestCase("MEASURE INTERVAL") {

            protected void test() {
                new Thread(new Runnable() {

                    public void run() {
                        ISubject sub1 = new PassthroughSubject();
                        sub1.to(new MeasureInterval()).sink(new Sink() {

                            private long[] keys = {100, 50, 100, 150, 250};
                            private int i = 0;

                            protected void onValue(Object value) {
                                MeasureInterval.Interval interval = (MeasureInterval.Interval) value;
                                long millis = interval.getIntervalMillis();
                                long key = keys[i];
                                S.debug("MI MILLIS + " + millis);
                                assertEqual(true, millis >= key && millis < key + 20);
                                i++;
                            }

                            protected void onCompletion(Completion completion) {
                                pass();
                            }
                        });

                        S.sleep(100);
                        sub1.sendValue("3");
                        S.sleep(50);
                        sub1.sendValue("4");
                        S.sleep(100);
                        sub1.sendValue("1");
                        S.sleep(150);
                        sub1.sendValue("8");
                        S.sleep(250);

                        sub1.sendCompletion(new Completion(true));
                    }
                }).start();
            }
        };
    }

    private IPublisher testRetry() {
        return new TestCase("RETRY") {

            protected void test() {
                new Thread(new Runnable() {

                    private IPublisher makePublisher() {
                        return new Publisher() {

                            private int counter = 0;

                            public void subscriptionDidRequestValues(ISubscription subscription, Demand demand) {
                                if (counter++ < 3) {
                                    subscription.getSubscriber().receiveCompletion(new Completion(false));
                                } else {
                                    subscription.getSubscriber().receiveInput("HELLO");
                                    subscription.getSubscriber().receiveCompletion(new Completion(true));
                                }
                            }
                        };
                    }

                    public void run() {
                        IPublisher sub1 = makePublisher();

                        sub1.to(new Retry(6)).sink(new Sink() {

                            private int i = 0;

                            protected void onValue(Object value) {
                                assertEqual(value, "HELLO");
                                i++;
                            }

                            protected void onCompletion(Completion completion) {
                                assertEqual(1, i);
                                assertEqual(true, completion.isSuccess());
                            }
                        });

                        sub1 = makePublisher();
                        sub1.to(new Retry(2)).sink(new Sink() {

                            protected void onValue(Object value) {
                                S.println(value);
                                fail();
                            }

                            protected void onCompletion(Completion completion) {
                                assertEqual(false, completion.isSuccess());
                                pass();
                            }
                        });

                    }
                }).start();
            }
        };
    }

    private IPublisher testAssertNoFailure() {
        return new TestCase("ASSERT NO FAILURE") {

            protected void test() {
                new Thread(new Runnable() {

                    public void run() {
                        ISubject sub1 = new PassthroughSubject();

                        sub1.to(new AssertNoFailure("AssertNoFailure :(")).sink(new Sink() {

                            private int i = 0;

                            protected void onValue(Object value) {
                                assertEqual(value, "1");
                                i++;
                            }

                            protected void onCompletion(Completion completion) {
                                assertEqual(1, i);
                                assertEqual(true, completion.isSuccess());
                                pass();
                            }
                        });

                        sub1.sendValue("1");
                        sub1.sendCompletion(new Completion(true));
                    }
                }).start();
            }
        };
    }

    private IPublisher testCatch() {
        return new TestCase("CATCH") {

            protected void test() {
                new Thread(new Runnable() {

                    public void run() {
                        ISubject sub1 = new PassthroughSubject();
                        sub1.to(new Catch() {

                            protected IPublisher publisherForFailure(Completion completion) {
                                return new Publisher() {

                                    public void subscriptionDidRequestValues(ISubscription subscription,
                                            Demand demand) {
                                        final ISubscription fs = subscription;
                                        (new Thread(new Runnable() {

                                            public void run() {
                                                S.sleep(200);
                                                fs.getSubscriber().receiveInput("CATCH");
                                                fs.getSubscriber().receiveCompletion(new Completion(true));
                                            }
                                        })).start();
                                    }
                                };
                            }
                        }).sink(new Sink() {

                            private String result = "";

                            protected void onValue(Object value) {
                                result += value;
                            }

                            protected void onCompletion(Completion completion) {
                                assertEqual("12CATCH", result);
                                assertEqual(true, completion.isSuccess());
                                pass();
                            }
                        });

                        sub1.sendValue("1");
                        sub1.sendValue("2");
                        sub1.sendCompletion(new Completion(false));
                        sub1.sendValue("3");
                        sub1.sendCompletion(new Completion(true));

                    }
                }).start();
            }
        };
    }

    private IPublisher testDropUntil() {
        return new TestCase("DROP UNTIL OUTPUT") {

            protected void test() {
                new Thread(new Runnable() {

                    private String result = "";

                    public void run() {
                        ISubject sub1 = new PassthroughSubject();
                        ISubject sub2 = new PassthroughSubject();

                        sub1.to(new DropUntilOutput(sub2)).sink(new Sink() {

                            protected void onValue(Object value) {
                                result += value;
                            }

                            protected void onCompletion(Completion completion) {
                                assertEqual("45", result);
                                assertEqual(true, completion.isSuccess());
                                pass();
                            }
                        });

                        sub1.sendValue("1");
                        sub1.sendValue("2");
                        sub1.sendValue("3");
                        sub2.sendValue("X");
                        sub1.sendValue("4");
                        sub1.sendValue("5");
                        sub2.sendValue("Y");
                        sub2.sendCompletion(new Completion(false));
                        sub1.sendCompletion(new Completion(true));
                    }
                }).start();
            }
        };
    }

    private IPublisher testPrefixUntil() {
        return new TestCase("PREFIX UNTIL OUTPUT") {

            protected void test() {
                new Thread(new Runnable() {

                    private String result = "";

                    public void run() {
                        ISubject sub1 = new PassthroughSubject();
                        ISubject sub2 = new PassthroughSubject();

                        sub1.to(new PrefixUntilOutput(sub2)).sink(new Sink() {

                            protected void onValue(Object value) {
                                result += value;
                            }

                            protected void onCompletion(Completion completion) {
                                assertEqual("123", result);
                                assertEqual(true, completion.isSuccess());
                                pass();
                            }
                        });

                        sub1.sendValue("1");
                        sub1.sendValue("2");
                        sub1.sendValue("3");
                        sub2.sendValue("X");
                        sub1.sendValue("4");
                        sub1.sendValue("5");
                        sub2.sendValue("Y");
                        sub2.sendCompletion(new Completion(false));
                        sub1.sendCompletion(new Completion(true));
                    }
                }).start();
            }
        };
    }

    private IPublisher testRecord() {
        return new TestCase("RECORD") {

            protected void test() {
                new Thread(new Runnable() {

                    ISubject sub1 = new PassthroughSubject();
                    long keysI[] = new long[]{
                        700 + 100,
                        700 + 100 + 200,
                        700 + 100 + 200 + 300,
                        700 + 100 + 200 + 300 + 100
                    };
                    String keysV[] = new String[]{"1", "2", "3"};
                    long time = (new java.util.Date()).getTime();
                    int i = 0;

                    public void run() {
                        sub1.to(new Record()).sink(new Sink() {

                            protected void onValue(Object value) {
                                long interval = keysI[i];
                                Object val = keysV[i];

                                long delta = ((new java.util.Date()).getTime()) - time;
                                S.debug("TIME: " + delta + " KEY " + interval + " VAL " + value);

                                assertEqual(true, delta >= interval && delta <= interval + 100);
                                assertEqual(val, value);

                                i++;
                            }

                            protected void onCompletion(Completion completion) {
                                assertEqual(true, completion.isSuccess());
                                long interval = keysI[i];

                                long delta = ((new java.util.Date()).getTime()) - time;
                                assertEqual(true, delta >= interval && delta <= interval + 100);
                                S.debug("TIME: " + delta + " KEY " + interval + " COMPL " + completion.isSuccess());

                                pass();
                            }
                        });

                        S.sleep(100);
                        sub1.sendValue("1");
                        S.sleep(200);
                        sub1.sendValue("2");
                        S.sleep(300);
                        sub1.sendValue("3");
                        S.sleep(100);
                        sub1.sendCompletion(new Completion(true));
                    }
                }).start();
            }
        };
    }

    private IPublisher testSubRecSchedulers() {
        return new TestCase("SUBSCRIBE/RECEIVE_ON") {

            protected void test() {
                final ISubject sub1 = new PassthroughSubject();
                sub1 //					.subscribeOn(new DispatchQueue(10))
                        .receiveOn(new DispatchQueue(10)).to(new Map() {

                    public Object mapValue(Object value) {
                        S.sleep(1000);
                        String s = "MAPPED " + value + " ON THREAD " + Thread.currentThread();
                        S.debug(s);
                        S.sleep(1000);
                        return value + "" + value;
                    }
                }).receiveOn(new DispatchQueue(10)).sink(new Sink() {

                    protected void onValue(Object value) {
                        S.sleep(1000);
                        S.debug("VALUE " + value + " ON THREAD " + Thread.currentThread());
                    }

                    protected void onCompletion(Completion completion) {
                        S.sleep(1000);
                        S.debug("COMPLETION " + completion.isSuccess() + " ON THREAD " + Thread.currentThread());
                        pass();
                    }
                });

                String s = "STARTED ON THREAD " + Thread.currentThread();
                S.debug(s);

                DispatchQueue q = new DispatchQueue(3);

                for (int i = 0; i < 10; i++) {
                    sub1.sendValue("" + i);
                }

                q.async(3000, new Runnable() {

                    public void run() {
                        sub1.sendCompletion(new Completion(true));
                    }
                });
            }
        };
    }
}
