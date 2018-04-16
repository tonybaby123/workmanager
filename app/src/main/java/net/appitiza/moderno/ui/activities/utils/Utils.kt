package net.appitiza.moderno.ui.activities.utils

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.preference.PreferenceManager
import android.provider.Settings
import android.support.v7.app.AlertDialog
import net.appitiza.moderno.R
import java.text.DateFormat
import java.util.*

class Utils {

    companion object {
        val KEY_REQUESTING_LOCATION_UPDATES = "requesting_locaction_updates"

        /**
         * Returns true if requesting location updates, otherwise returns false.
         *
         * @param context The [Context].
         */
        fun requestingLocationUpdates(context: Context): Boolean {
            return PreferenceManager.getDefaultSharedPreferences(context)
                    .getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false)
        }

        /**
         * Stores the location updates state in SharedPreferences.
         * @param requestingLocationUpdates The location updates state.
         */
        fun setRequestingLocationUpdates(context: Context, requestingLocationUpdates: Boolean) {
            PreferenceManager.getDefaultSharedPreferences(context)
                    .edit()
                    .putBoolean(KEY_REQUESTING_LOCATION_UPDATES, requestingLocationUpdates)
                    .apply()
        }

        /**
         * Returns the `location` object as a human readable string.
         * @param location  The [Location].
         */
        fun getLocationText(location: Location?): String {
            return if (location == null)
                "Unknown location"
            else
                "(" + location.latitude + ", " + location.longitude + ")"
        }

        fun getLocationTitle(context: Context): String {
            return context.getString(R.string.location_updated,
                    DateFormat.getDateTimeInstance().format(Date()))
        }

        fun reDirectToPermissionScreen(activity: Activity, message: String, positiveButtonText: String) {
            val alert: AlertDialog
            val builder = AlertDialog.Builder(activity)
            builder.setTitle(activity.getString(R.string.app_name))
            builder.setMessage(message)
            builder.setCancelable(false)
            builder.setPositiveButton(positiveButtonText) { dialog, which ->
                dialog.dismiss()
                activity.finish()
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", activity.packageName, null)
                intent.data = uri
                activity.startActivity(intent)
            }


            alert = builder.create()
            alert.show()
        }
    }
}