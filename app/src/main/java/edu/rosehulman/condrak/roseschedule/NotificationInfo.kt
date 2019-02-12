package edu.rosehulman.condrak.roseschedule

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NotificationInfo (var day: Int, var dayIndex: Int, var periodIndex: Int) : Parcelable