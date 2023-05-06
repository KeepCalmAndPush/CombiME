# CombiME
A reactive programming framework for Java ME phones. Inspired by Apple's Combine. 

Implements Combine's Publisher -> Subscription -> Subscriber and Backpressure models. 

Has full set of reactive operators you might expect: mapping, reducing, filtering, timing; math and sequence operations. Covered with unit tests. 

This implementation tightly follows this guide to Apple's Combine: https://heckj.github.io/swiftui-notes/, kudos to Joe Heck.

## Note
As stated above, this is a piece of software for Java ME phones. The version of Java that can be used is rather old: 1.3. This means that there is no Generics, no Collection framework, no modern Java features like lambdas, even no `enums`. Atop of that are the limitations of a Mobile platform itself: no reflection, no floating point calculations, etc. The purpose of the project is to make (out of curiosity and self-didaction) a proof of concept that reactive programming is feasible even on older devices, when there was no trend of reactive programming in mobile, to feel the classic mobile development as modern and reactive as possible. Overcoming the constraints was a great driver too. For example, the lack of lambda (closures, block) expressions is handled via usage of Java's anonymous inner classes.

## Structure
### KEK
The sources are located in `ru.asolovyov.combime` package and down the respective subpackages. 

<img width="338" alt="Снимок экрана 2022-08-25 в 13 54 45" src="https://user-images.githubusercontent.com/13520824/186647527-f456613e-cb1f-4450-a7c6-c4510b07b08d.png"><img width="624" alt="Снимок экрана 2022-08-25 в 14 08 42" src="https://user-images.githubusercontent.com/13520824/186649350-2f7805b6-d733-49b7-b092-d125f4b143a9.png">

