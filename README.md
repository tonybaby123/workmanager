# "Moderno" a Firestore implementation using Kotlin

You can use this application as a reference for implementing Frestore.

## Prerequisite
To continue,you need some basic idea about Kotlin and Firestore.

Here is a video on Kotlin


[![IMAGE ALT TEXT HERE](https://i1.ytimg.com/vi/ZIHnQQsfvD4/0.jpg)](https://www.youtube.com/watch?v=ZIHnQQsfvD4&t=20s)


[Click Here to know more about Firestore](https://firebase.google.com/docs/firestore/quickstart")

## Getting Started
In fire base there are mainly two main database concepts.One is [Realtime database](https://firebase.google.com/docs/database/")  and the other one is [Firestore](https://firebase.google.com/docs/firestore/").Firestore is in beta mode,which means code can change.In this application,We are concentrating on Firestore.
[Click Here to know more about the deference between Realtimedatabase and Firestore](https://firebase.google.com/docs/database/rtdb-vs-firestore")
### Project creation in firebase
**Sptep 1.** Create a project in Firebase [Link](https://firebase.google.com/docs/firestore/quickstart"):
**Sptep 2.** Provide  details like Packagename,SHA1 etc to configure
**Sptep 2.** On completing step2,a file named `google-services.json`.Download this file and add to the app folder.With this process your done with configuring firebase.


### Usage
#### To use the signaturepad
Copy the below code to use signnaturepad

```
<net.appitiza.android.drawingpad.drawpad.views.SignatureView
        android:id="@+id/signature_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        attr:clearOnDoubleTap="true"
        attr:penColor="@android:color/holo_blue_dark"
        attr:penMaxWidth="9dp"
        attr:penMinWidth="5dp"
        attr:speedimpressionWeight=".1" />
```

You can use attributes to adjust the features of signature pad
Example 
```
        attr:penMaxWidth="9dp"
        attr:penMinWidth="5dp"
```
With above code, you can adjust the pen width

#### To use the drawingpad
Copy the below code to use drawingpad

```
<net.appitiza.android.drawingpad.drawpad.views.DrawingView
        android:id="@+id/drawing_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```


## ScreenShot

![Alt Text](https://github.com/appitiza/SignatureApp/blob/master/images/drawing.gif)
![Alt Text](https://github.com/appitiza/SignatureApp/blob/master/images/signature.gif)

[Demo Download](https://github.com/appitiza/SignatureApp/blob/master/apk/signatureapp.apk)

## License


Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
