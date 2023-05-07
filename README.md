# TummyUI
Declarative UI framework for Java ME powered phones. Inspired by Apple's SwiftUI. Requires only MIDP 1.0/CLDC 1.1 so is compatible with most (if not all) of the phones. 

Uses [CombiME](https://github.com/KeepCalmAndPush/CombiME) - reactive programming framework for JavaME phones, inspired by Apple's Combine.

Due to limitations of the MIDP 1.0, TummyUI is split into two parts: UI-part (forms), to build user interface with standard components only, and CG-part (graphics), providing custom drawing capabilities for your own layouts and controls.

## Note
The purpose of the project is to make a proof of concept that declarative layout is achievable even on older devices, when there was no trend of declarative reactive programming in mobile. This is not a strict implementation of all the possibilities of SwiftUI, rather than a syntactical imitation, aiming to feel classic mobile development as modern as possible. The title of the project derives from SwiftUI through J2MEUI ('ʤeɪ tuː miː ui') to TummyUI.

## UI Part (Forms)
The UI-part is split between two packages: `ru.asolovyov.tummyui.forms` (core infrastructural classes) and `ru.asolovyov.tummyui.forms.views` (UI components themselves).
###Core
The entry point for all the TummyUI is the `UIMIDlet` class. Abstract descendant of `javax.microedition.midlet.MIDlet`, it requires to implement the single method: `protected abstract Displayable content();` which must return the first screen of your app. Also `UIMIDlet` notifies its listeners of MIDlet lifecycle events (start, pause, destroy) by virtue of CombiME's `PassthroughSubjects`. 

Further screens can be easily presented by using the navigation capabilities of TummyUI: see how `UIForm`s conform to `UINavigatable` or use a `UIDisplayableNavigationWrapper` to provide any `javax.microedition.lcdui.Displayable` as a TummyUI's navigatable object. 

One way of triggering the navigation is usage of `UICommand`s. These objects extend `javax.microedition.lcdui.Command` with inline handlers, which make the callback experiense more iOS and closure-like. 


Worth noting the `UIEnvironment` class. It works like an app-wide session where you can put your objects keyed by `String` identifiers. It also keeps a reference to a current `UIMIDlet`.

Finally, the `UI` class provides a set of static methods of instantiating Views, so you do not need to create them with `new` keyword, making the code looking more Swifty. Here is an example of a simple UI form, with navigation to TextBox and reactive handling of editing events:

```
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

[![Hello world](https://user-images.githubusercontent.com/13520824/236672342-381b3838-fa0e-4947-868e-4ee23a2df217.mov)]


