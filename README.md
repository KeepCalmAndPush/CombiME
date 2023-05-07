# TummyUI
Declarative UI framework for Java ME powered phones. Inspired by Apple's SwiftUI. Requires only MIDP 1.0/CLDC 1.1 so is compatible with most (if not all) of the phones. 

Uses [CombiME](https://github.com/KeepCalmAndPush/CombiME) - reactive programming framework for JavaME phones, inspired by Apple's Combine.

Due to limitations of the MIDP 1.0, TummyUI is split into two parts: UI-part (forms), to build user interface with standard components only, and CG-part (graphics), providing custom drawing capabilities for your own layouts and controls.

## Note
The purpose of the project is to make a proof of concept that declarative layout is achievable even on older devices, when there was no trend of declarative reactive programming in mobile. This is not a strict implementation of all the possibilities of SwiftUI, rather than a syntactical imitation, aiming to feel classic mobile development as modern as possible. The title of the project derives from SwiftUI through J2MEUI ('ʤeɪ tuː miː ui') to TummyUI.

## UI Part (Forms)
The UI-part is split between two packages: `ru.asolovyov.tummyui.forms` (core infrastructural classes) and `ru.asolovyov.tummyui.forms.views` (UI components themselves).
![Снимок экрана 2023-05-07 в 12 44 49](https://user-images.githubusercontent.com/13520824/236673109-ce71da0a-a27d-4e61-acf8-1e5c92db64d9.png)
### Core
The entry point for all the TummyUI is the `UIMIDlet` class. Abstract descendant of `javax.microedition.midlet.MIDlet`, it requires to implement a single method: `protected abstract Displayable content();` which must return the first screen of your app. Also `UIMIDlet` notifies its listeners of MIDlet lifecycle events (start, pause, destroy) by virtue of CombiME's `PassthroughSubjects`. 

Further screens can be easily presented by using the navigation capabilities of TummyUI: see how `UIForm`s conform to `UINavigatable` or use a `UIDisplayableNavigationWrapper` to provide any `javax.microedition.lcdui.Displayable` as a TummyUI's navigatable object. 

One way of triggering the navigation is usage of `UICommand`s. These objects extend `javax.microedition.lcdui.Command` with inline handlers, which make the callback experiense more iOS and closure-like. 


Worth noting the `UIEnvironment` class. It works like an app-wide session where you can put your objects keyed by `String` identifiers. It also keeps a reference to a current `UIMIDlet`.

Finally, the `UI` class provides a set of static methods of instantiating Views, so you do not need to create them with `new` keyword, making the code looking more Swifty. Here is an example of a simple UI form, with navigation to TextBox and reactive handling of editing events:

```java
public class FormsTest extends UIMIDlet {

    protected Displayable content() {
        return UI.Form("Forms",
                    UI.StringItem(UIEnvironment.put("hello-world-key", "Hello, world!"))
               )
               .navigationCommand(
                    "Change it!", "Back",
                     UI.TextBox("UITextBox", UIEnvironment.string("hello-world-key"))
               );
    }

}
```
https://user-images.githubusercontent.com/13520824/236672918-4da2dc0c-8729-49f7-ba5b-d9046a056481.mov

### Views

Views in TummyUI consist of wrappers over standard J2ME components (Form, Image, TextBox etc) and add some new container classes: `UIIf`, `UIForEach` and `UIGroup`.

#### Containers
`UIForm` is a workhorse of all UI-layout. It is a subclass of `javax.microedition.lcdui.Form` and allows placing of other UI-components, represented by descendants of `UIItem`.
Each `UIItem` may become hidden/visible and raise a `needsRelayout` flag if some other change occured, that needs to be represented in layout. `UIIForm` listens to this changes reactively and redraws its contents if needed.

The simplest container class is `UIGroup` which holds an array of other `UIItem`s, so they can be added or removed from the interface at once.

`UIIf` lets you show or hide portions of the interface depending on a state of its `Bool` binding. `UIGroup` comes in handy here, allowing you to batch-control the visibility of components.

Finally, `UIForEach` lets you dynamically transform your reactive subject (`Arr`) of models into a list of `UIItem`s. This is possible by providing an instance of `UIGroup.ItemFactory`.

#### Components
Regular visual components are quite self-explanatory. In `ru.asolovyov.tummyui.forms.views` TummyUI has an assortment of `UIAlert`, `UIChoiceGroup`, `UIDateField`, `UIGauge`, `UIImageItem`, `UIStringItem`, `UITextField`, `UITextBox`. These classes wrap eponymous system components. Here is an extensive example of all available views. Left command button rises an Alert, right command triggers logic in If container. Note that If container manages two ForEach nested containers.

```java
public class FormsTest extends UIMIDlet {

    private Bool alertTrigger = new Bool(false);
    private Bool isOdd = new Bool(true);
    private Arr oddValues = new Arr(new Object[]{"1", "3"});
    private Arr evenValues = new Arr(new Object[]{"2", "4"});

    protected Displayable content() {
        return UI.Form("Forms",
                UI.If(isOdd)
                    .Then(
                        UI.ForEach(oddValues, new ItemFactory() {
                            public UIItem itemFor(Object viewModel) {
                                return UI.StringItem("Odd:", (String) viewModel);
                            }
                    })).Else(
                        UI.ForEach(evenValues, new ItemFactory() {
                            public UIItem itemFor(Object viewModel) {
                                return UI.StringItem("Even:", (String) viewModel);
                            }
                    })),
                UI.DateField("Today is:", DateField.DATE_TIME, new Date()),

                UI.Group(
                    UI.StringItem(UIEnvironment.put("hello-world-key", "Hello, world!")),
                    UI.TextField(UIEnvironment.string("hello-world-key")),
                    UI.Gauge("Gauge", true, 1, 10)),
                    UI.ChoiceGroup("Are you a", ChoiceGroup.EXCLUSIVE, new ListItem[]{
                        new ListItem("Cat person", null, true),
                        new ListItem("Dog person", null, false)
                }),

                UI.ImageItem(null, "res/1.png", 0, "Cat")
               )
               .alert(alertTrigger, UI.Alert("ALERT!", "Hello!", null, AlertType.ALARM))
               .command(new UICommand("Alert", new UICommand.Handler() {
                    public void handle() {
                        alertTrigger.setBool(!alertTrigger.getBoolean());
                    }
                }))
                .command(new UICommand("If", new UICommand.Handler() {
                    public void handle() {
                        isOdd.setBool(!isOdd.getBoolean());
                    }
                }));
    }
}
```


https://user-images.githubusercontent.com/13520824/236685997-0310aa6f-f8b3-4023-9623-8fffcb0d24fd.mov

