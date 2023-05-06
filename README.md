# CombiME
A reactive programming framework for Java ME phones. Inspired by Apple's Combine. 

Implements Combine's Publisher -> Subscription -> Subscriber and Backpressure models. 

Has full set of reactive operators you might expect: mapping, reducing, filtering, timing; math and sequence operations. Covered with unit tests. 

This implementation tightly follows this guide to Apple's Combine: https://heckj.github.io/swiftui-notes/, kudos to Joe Heck.

## Note
As stated above, this is a piece of software for Java ME phones. The version of Java that can be used is rather old: 1.3. This means that there is no Generics, no Collection framework, no modern Java features like lambdas, even no `enums`. Atop of that are the limitations of a Mobile platform itself: no reflection, no floating point calculations, etc. The purpose of the project is to make (out of curiosity and self-didaction) a proof of concept that reactive programming is feasible even on older devices, when there was no trend of reactive programming in mobile, to feel the classic mobile development as modern and reactive as possible. Overcoming the constraints was a great driver too. For example, the lack of lambda (closures, block) expressions is handled by usage of Java's anonymous inner classes.

## Structure
The sources can be found in `ru.asolovyov.combime` and down the respective subpackages. 

### API
The core interfaces are located in `ru.asolovyov.combime.api` package. They match the corresponding Combine interfaces and are self-explaining. The most notable are `IPublisher` (the producer of values), `ISubscriber` (the consumer), `ISubscription` (the way for Subscriber to control the Publisher) and `IOperator` (being a Subscriber and a Publisher at the same time, provides a way to transform values received from Publisher and feed the transformed values down the reactive chain). As `IPublisher` produces values by its inner logic, the `ISubject` is a way for you to provide your values to the chain, making your legacy code reactive. 

`IPublisher` starts producing values when someone calls its `sink(ISubscriber subscriber)`. Operator starts listening to publisher's values when is being fed to publisher's `to(IOperator operator)` method. You may also redirect the output of a publisher to your own subject via `route(ISubject subject)`.

Also, many non-modifying reactive operators like `merge`, `compactMap`, `combineLatest` and so on are implemented as declared as instance methods in `IPublisher`:

```
public interface IPublisher extends Identifiable {
    public IPublisher to(IOperator operator);
    
    public ISubscription sink(ISubscriber subscriber);
    public ISubscription route(ISubject subject);

    public IPublisher receiveOn(Scheduler scheduler);

    public IPublisher print();
    public IPublisher print(String prefix);
    public IPublisher print(String prefix, PrintStream printStream);
    
    public IPublisher merge(IPublisher publisher);
    public IPublisher combineLatest(IPublisher publisher);

    public IPublisher switchToLatest();
    public IPublisher zip(IPublisher publisher);

    public IPublisher drop(int n);
    public IPublisher prefix(int n);

    public IPublisher next();

    public IPublisher assertNoFailure();
    public IPublisher assertNoFailure(String message);

    public IPublisher retry(int count);

    public IPublisher compactMap();
    public IPublisher removeDuplicates();
    public IPublisher replaceEmpty(Object replacement);
    public IPublisher replaceError(Object replacement);

    public IPublisher contains(Object object);

    public IPublisher count();
    public IPublisher max();
    public IPublisher min();

    public IPublisher collect(int count);
    public IPublisher collect(long millis);
    public IPublisher collect(int count, long millis);

    public IPublisher ignoreOutput();

    public IPublisher debounce(long millis);
    public IPublisher delay(long millis);
    public IPublisher delay(long millis, Scheduler scheduler);
    public IPublisher throttle(long millis);
    public IPublisher throttle(long millis, boolean latest);
    public IPublisher throttle(long millis, boolean latest, Scheduler scheduler);
    public IPublisher timeout(long millis);
    public IPublisher timeout(long millis, Scheduler scheduler);

    public Int asInt();
    public Str asStr();
    public Bool asBool();
    public Arr asArr();
}
```

### Bindings
As mentioned, Java 1.3 has its limitations. One of those is an abscense of Generics. So CombiME provides almost no type-checking, and ubiquitous type casts for values in a chain is an irremovable evil. Nonetheless, CombiME provides `CurrentValueSubject`s for the most common data types in Java: `int`, `String`, `boolean`, `Object` and array of `Object`s. Primitive values are boxed to `Int` and `Bool`. These subjects are intended to be used as data bindings in client apps. If you find the assortment of bindings scarse, please feel free to make pull requests :)

### Common
The implementation for interfaces from API lies in `ru.asolovyov.combime.common`. The `S.java` class is a just a collection of intra-project neat utils, handling (un)boxing, printing and alike. Implementation of `IOperator` is located in `ru.asolovyov.combime.operators` and is the only inhabitant of that package.

### Debugging
By virtue of contents of `ru.asolovyov.combime.debugging` you can inspect what is going on inside your chain. `Print` operator, while inserted in a chain, prints the values going through. `HandleEvents` is an abstract class. You provide a `Publisher` with a descendant to be able to monitor the events of receiveng a Subscription, Demand, Output, Completion and Cancellation.

### Subjects
`ru.asolovyov.combime.subjects` lists two crucial subjects: `PassthroughSubject` and `CurrentValueSubject`. The former passes a value you provide down the operators chain and forgets it. The latter keeps the latest value and provides it to any new connected subscribers.

### Publishers
Classic publishers are exposed in `ru.asolovyov.combime.publishers`. The most trivial are `Just` (provides one value and completes), `Empty` (completes immediately) and `Fail` (fails immediately). Close to Just is `Sequence`, which accepts an array of elements and feeds them one-by-one to subscribers. `Record` listens to other publisher, collects its values, and after completion serves them at once. `Future` and `Deferred` provide values of a once-executed potentially long-running task, with the difference that `Future` starts the task immediately and the `Deferred` waits for the first subscriber to request values.

### Scheduling
CombiME has a basic support of scheduling. Inspect `ru.asolovyov.threading` for details. The main facility is `DispatchQueue` class which resembles the usage of dispatch queues in iOS. It allows you to spawn you runnable tasks consequently/simultaneously and with any delay. `Clock` is a convenient way to dispatch your tasks to execute once (or repeatedly, each time) after a given time interval.

### Operators
CombiME features full set of reactive operators. They are grouped in respective packages (combining, error handling, filtering, mapping, matching, math, reducing, sequence, timing) and implement the exact behavior stated in their names.

![Снимок экрана 2023-05-06 в 15 40 49](https://user-images.githubusercontent.com/13520824/236625398-d375d4d6-5615-4544-b8a5-289b47419aef.png)

### Try it out
The comprehensive set of samples is provided in `ru.asolovyov.combime.tests`. There is a runnable MIDlet `Tests` which covers all the operators and functions in a unit test manner and generates a test report. Here is an example of one such tests:

```
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
```
