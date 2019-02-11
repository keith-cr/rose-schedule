package edu.rosehulman.condrak.roseschedule

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NotificationInfo (var day: Int, var period: ClassPeriod, var scheduleTiming: ScheduleTiming,
                             var notificationId: Int) : Parcelable