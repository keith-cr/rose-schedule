package edu.rosehulman.condrak.roseschedule

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ClassPeriod (var periodNumber: Int, var className: String, var classLocation: String): Parcelable {
    fun getPeriodText(): String  {
        var prefix = when (periodNumber) {
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
        return  "$prefix Period"
    }

    fun hasLocation(): Boolean {
        return classLocation != ""
    }
}