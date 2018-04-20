package net.appitiza.workmanager.ui.model

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ServerTimestamp

class NotificationData {
    var notificationId: String? = null
    var title: String? = null
    var message: String? = null
    var to: String? = null
    var time: String = ""

}