package edu.rosehulman.condrak.roseschedule

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ClassPeriod (var periodNumber: Int = 0, var className: String = "", var classLocation: String = "",
                        var isFree: Boolean = false, var hasNotification: Boolean = false,
                        var hasAlarm: Boolean = false, var minutesBefore: Int = 10): Parcelable {
    constructor(periodNumber: Int): this(periodNumber, "Free", "", true)

    constructor(periodNumber: Int, className: String, classLocation: String):
            this(periodNumber, className, classLocation, false)

    fun periodText(): String  {
        val prefix = shortPeriodText()
        return  "$prefix Period"
    }

    fun shortPeriodText(): String {
        return when (periodNumber) {
            1 -> "1st"
            2 -> "2nd"
            3 -> "3rd"
            4 -> "4th"
            5 -> "5th"
            6 -> "6th"
            7 -> "7th"
            8 -> "8th"
            9 -> "9th"
            10 -> "10th"
            else -> "${periodNumber}th"
        }
    }

    fun hasLocation(): Boolean {
        return classLocation != ""
    }
}