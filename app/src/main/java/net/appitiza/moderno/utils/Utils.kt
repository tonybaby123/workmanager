package net.appitiza.moderno.utils

import android.location.Location
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class Utils {
    companion object {
        fun convertDate(milli: Long, dateFormat: String): String {
            val format = SimpleDateFormat(dateFormat, Locale.ENGLISH)
            var calendar = Calendar.getInstance()
            calendar.timeInMillis = milli
            val value = format.format(calendar.time)
            return value
        }

        fun convertHours(millis: Long): String {
            val hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)))
            return hms
        }
        fun convertDays(startmillis: Long,stopmillis: Long): Long {
            val msDiff = stopmillis - startmillis
            val daysDiff = TimeUnit.MILLISECONDS.toDays(msDiff)
            return daysDiff
        }

        fun isWithinRange(centerLatitude: Double, centerLongitude: Double, testLatitude: Double, testLongitude: Double, range: Float): Boolean {
            val results = FloatArray(1)
            Location.distanceBetween(centerLatitude, centerLongitude, testLatitude, testLongitude, results)
            val distanceInMeters = results[0]
            return distanceInMeters < (range * 1000)
        }
        fun getDate(date: String,format: String): Date {
            val format = SimpleDateFormat(format,Locale.ENGLISH)
            val value: Date = format.parse(date)
            return value
        }
    }
}