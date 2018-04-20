# "Moderno" a serverless mobile android application using Kotlin and firestore

You can use this application as a reference for implementing Frestore.

## Prerequisite
To continue,you need some basic idea about Kotlin and Firestore.

Here is a video on Kotlin


[![IMAGE ALT TEXT HERE](https://i1.ytimg.com/vi/ZIHnQQsfvD4/0.jpg)](https://www.youtube.com/watch?v=ZIHnQQsfvD4&t=20s)


[Click Here to know more about Firestore](https://firebase.google.com/docs/firestore/quickstart")

## Getting Started
In Firebase there are mainly two main database concepts.One is [Realtime database](https://firebase.google.com/docs/database/")  and the other one is [Firestore](https://firebase.google.com/docs/firestore/").Firestore is in beta mode,which means code can change.In this application,We are concentrating on Firestore.
[Click Here to know more about the deference between Realtime database and Firestore](https://firebase.google.com/docs/database/rtdb-vs-firestore")


### Project creation in firebase
**Sptep 1.** Create a project in Firebase [Link](https://firebase.google.com/docs/firestore/quickstart"):
**Sptep 2.** Provide  details like Packagename,SHA1 etc to configure
**Sptep 2.** On completing step2,a file named `google-services.json`.Download this file and add to the app folder.With this process your done with configuring firebase.

### Push Notification in serverless application

We are going to develope a cloud function and host them in Googe cloud functions.On hosting you can view them in your firebase console.
To start with,you need to  installed in your local system.With help of firebase tool you need to create a cloud function.On completeing this process you can upload it in to firebase cloud function.
**Sptep 1.** Install node.js in your local system[Click Here to download Nodejs](https://nodejs.org/en/") 
**Sptep 1.** Make folder for 


### Note:
I have removed `google-services.json` file from this project.You need to include it.
As i said above,Firestore is in beta so there are possibilities change.I will be updating them.
Methods associated with push notification is only tested in Windows system.


## ScreenShot

![Alt Text](https://github.com/appitiza/SignatureApp/blob/master/images/drawing.gif)
![Alt Text](https://github.com/appitiza/SignatureApp/blob/master/images/signature.gif)

[Demo Download](https://github.com/appitiza/SignatureApp/blob/master/apk/signatureapp.apk)

## License


Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
