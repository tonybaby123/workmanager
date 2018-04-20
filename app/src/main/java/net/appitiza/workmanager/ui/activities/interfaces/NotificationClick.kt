package net.appitiza.workmanager.ui.activities.interfaces

import android.app.Notification
import net.appitiza.workmanager.ui.model.NotificationData
import net.appitiza.workmanager.ui.model.SiteListdata

interface NotificationClick {
    fun onClick(data:NotificationData)
}