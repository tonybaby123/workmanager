package net.appitiza.moderno.ui.activities.interfaces

import android.app.Notification
import net.appitiza.moderno.ui.model.NotificationData
import net.appitiza.moderno.ui.model.SiteListdata

interface NotificationClick {
    fun onClick(data:NotificationData)
}