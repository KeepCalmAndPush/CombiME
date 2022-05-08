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
import ru.asolovyov.combime.operators.combining.Merge;
import ru.asolovyov.combime.subjects.PassthroughSubject;
import ru.asolovyov.combime.operators.reducing.Reduce;
import ru.asolovyov.combime.operators.mapping.Scan;
import ru.asolovyov.combime.common.Sink;
import ru.asolovyov.combime.publishers.Empty;
import ru.asolovyov.combime.publishers.Fail;
import ru.asolovyov.combime.publishers.Future;
import ru.asolovyov.combime.publishers.Just;
import ru.asolovyov.combime.operators.mapping.Map;
import ru.asolovyov.combime.common.S;
import ru.asolovyov.combime.common.Subscriber;
import ru.asolovyov.combime.common.Task;
import ru.asolovyov.combime.operators.reducing.Collect;
import ru.asolovyov.combime.operators.filtering.CompactMap;
import ru.asolovyov.combime.operators.Delay;
import ru.asolovyov.combime.operators.combining.CombineLatest;
import ru.asolovyov.combime.operators.combining.Zip;
import ru.asolovyov.combime.operators.filtering.Filter;
import ru.asolovyov.combime.operators.mapping.FlatMap;
import ru.asolovyov.combime.operators.reducing.IgnoreOutput;
import ru.asolovyov.combime.operators.filtering.RemoveDuplicates;
import ru.asolovyov.combime.operators.filtering.ReplaceEmpty;
import ru.asolovyov.combime.operators.filtering.ReplaceError;
import ru.asolovyov.combime.operators.filtering.TryCompactMap;
import ru.asolovyov.combime.operators.filtering.TryFilter;
import ru.asolovyov.combime.operators.mapping.TryMap;
import ru.asolovyov.combime.operators.reducing.TryReduce;
import ru.asolovyov.combime.operators.filtering.TryRemoveDuplicates;
import ru.asolovyov.combime.operators.matching.AllSatisfy;
import ru.asolovyov.combime.operators.matching.Contains;
import ru.asolovyov.combime.operators.matching.ContainsWhere;
import ru.asolovyov.combime.operators.math.Count;
import ru.asolovyov.combime.operators.math.Max;
import ru.asolovyov.combime.operators.math.Min;
import ru.asolovyov.combime.operators.math.TryMax;
import ru.asolovyov.combime.operators.math.TryMin;
import ru.asolovyov.combime.publishers.Publisher;
import ru.asolovyov.combime.publishers.Sequence;

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

        final IPublisher tests[] = {
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
            this.testZip()
        };
        
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
                String result = new StringBuffer("\nTOTAL: ").append(tests.length).append(" RUN: ").append(run).append("\nFAILED: ").append(failed).append(" PASSED: ").append(passed).append("\n").append((passed * 1000 * 100) / (tests.length * 1000)).append("% OK.").toString();

                S.println(result);
                form.append(result);
            }
        });
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
                (new Sequence(new Object[]{"1", null, "2", null, "3"})).to(new CompactMap()).sink(new Sink() {

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
                (new Sequence(new Object[]{"1", null, "2", null, "3"})).to(new TryCompactMap() {

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
                (new Just("0")).to(new TryMap() {

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
                new Sequence(S.boxed(new int[]{1, 2, 3, 4, 5, 6})).to(new TryFilter() {

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
                (new Just("0")).to(new TryMap() {

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
                (new Sequence(new String[]{"1", "1", "2", null, null, null, "3", "3", "3", "hello", "hello"})).to(new TryRemoveDuplicates() {

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
                subj.to(new TryReduce("") {
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
                (new Sequence(new String[]{"1", "2", "3", "4", "5"}))
                        .to(new IgnoreOutput())
                        .sink(new Sink() {

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
                (new Sequence(new String[]{"1", "2", "3", "4", "5", "6", "7"}))
                        .to(new Collect((int)3))
                        .sink(new Sink() {

                    protected void onValue(Object value) {
                        if (i == 0) {
                            assertEqual(true, S.arraysEqual(new Object[]{"1", "2", "3"}, (Object[])value));
                        } else if (i == 1) {
                            assertEqual(true, S.arraysEqual(new Object[]{"4", "5", "6"}, (Object[])value));
                        } else if (i == 2) {
                            assertEqual(true, S.arraysEqual(new Object[]{"7"}, (Object[])value));
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
                pts
                        .to(new Collect((long) 300))
                        .sink(new Sink() {
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
                pts
                        .to(new Collect((long) 300, 4))
                        .sink(new Sink() {
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
                (new Sequence(S.boxed(new int[]{5, 2, 4, 1, 3})))
                        .to(new Min() {

                    protected boolean isNewValueLess(Object currentMin, Object newValue) {
                        int c = ((Integer)currentMin).intValue();
                        int n = ((Integer)newValue).intValue();

                        return n < c;
                    }
                })
                        .sink(new Sink() {

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
                (new Sequence(S.boxed(new int[]{4, 2, 5, 1, 3})))
                        .to(new Max() {

                    protected boolean isNewValueGreater(Object currentMax, Object newValue) {
                        int c = ((Integer)currentMax).intValue();
                        int n = ((Integer)newValue).intValue();
                        return n > c;
                    }
                })
                        .sink(new Sink() {

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
                (new Sequence(S.boxed(new int[]{5, 2, 4, 1, 3})))
                        .to(new TryMin() {

                    protected boolean isNewValueLess(Object currentMin, Object newValue) {
                        int c = ((Integer)currentMin).intValue();
                        int n = ((Integer)newValue).intValue();

                        if (n == 1) {
                            n = 1 / 0;
                        }

                        return n < c;
                    }
                })
                        .sink(new Sink() {

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
                (new Sequence(S.boxed(new int[]{4, 2, 5, 1, 3})))
                        .to(new TryMax() {

                    protected boolean isNewValueGreater(Object currentMax, Object newValue) {
                        int c = ((Integer)currentMax).intValue();
                        int n = ((Integer)newValue).intValue();

                        if (n == 1) {
                            n = 1 / 0;
                        }

                        return n > c;
                    }
                })
                        .sink(new Sink() {

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
                (new Sequence(S.boxed(new int[]{4, 2, 5, 1, 0, 3})))
                        .to(new Count())
                        .sink(new Sink() {

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
                (new Sequence(S.boxed(new int[]{4, 2, 5, 1, 0, 3})))
                        .to(new Contains(new Integer(1)))
                        .sink(new Sink() {

                    protected void onValue(Object value) {
                        count++;
                        assertEqual(true, ((Boolean)value).booleanValue());
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
                (new Sequence(S.boxed(new int[]{4, 2, 5, 1, 0, -4, 3})))
                        .to(new ContainsWhere() {
                    protected boolean doesSatisfy(Object obj) {
                        return ((Integer)obj).intValue() < 0;
                    }
                })
                        .sink(new Sink() {

                    protected void onValue(Object value) {
                        count++;
                        assertEqual(true, ((Boolean)value).booleanValue());
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
                (new Sequence(S.boxed(new int[]{4, 2, 5, -1, 0, 3})))
                        .to(new AllSatisfy() {
                    protected boolean doesSatisfy(Object obj) {
                        return ((Integer)obj).intValue() >= 0;
                    }
                })
                        .sink(new Sink() {

                    protected void onValue(Object value) {
                        count++;
                        assertEqual(false, ((Boolean)value).booleanValue());
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
                 new Object[]{"6", "5", "4"},
             };
             int i = 0;
             Thread t;
             
            protected void test() {
                final ISubject pub1 = new PassthroughSubject();
                final ISubject pub2 = new PassthroughSubject();
                final ISubject pub3 = new PassthroughSubject();

                (new CombineLatest(new IPublisher[]{pub1, pub2, pub3})).sink(new Sink() {
                    protected void onValue(Object value) {
                        Object[] ti = (Object[])test[i];
                        assertEqual(true, S.arraysEqual((Object[]) value,ti));
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
                        pub1.sendValue("1"); S.sleep(140);
                        pub2.sendValue("2"); S.sleep(20);
                        pub3.sendValue("3"); S.sleep(30);
                        pub3.sendValue("4"); S.sleep(90);
                        pub2.sendValue("5"); S.sleep(150);
                        pub1.sendValue("6"); S.sleep(100);

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
                 new Object[]{"6", "5", "4"},
             };
             int i = 0;
             Thread t;

            protected void test() {
                final ISubject pub1 = new PassthroughSubject();
                final ISubject pub2 = new PassthroughSubject();
                final ISubject pub3 = new PassthroughSubject();

                i = 0;

                (new Zip(new IPublisher[]{pub1, pub2, pub3})).sink(new Sink() {
                    protected void onValue(Object value) {
                        Object[] ti = (Object[])test[i];
                        assertEqual(true, S.arraysEqual((Object[]) value,ti));
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
                        pub1.sendValue("1"); S.sleep(140);
                        pub2.sendValue("2"); S.sleep(20);
                        pub3.sendValue("3"); S.sleep(30);
                        pub3.sendValue("4"); S.sleep(90);
                        pub2.sendValue("5"); S.sleep(150);
                        pub1.sendValue("6"); S.sleep(100);
                        pub2.sendValue("7"); S.sleep(150);
                        pub1.sendValue("8"); S.sleep(100);

                        pub2.sendCompletion(new Completion(true));
                        pub1.sendCompletion(new Completion(true));
                        pub3.sendCompletion(new Completion(true));
                    }
                });

                t.start();
            }
        };
     }

    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
    }

    protected void pauseApp() {
    }
}
