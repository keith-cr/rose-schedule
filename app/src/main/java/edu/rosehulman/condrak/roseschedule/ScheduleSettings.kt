package edu.rosehulman.condrak.roseschedule

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ScheduleSettings (var periodLength: Int = 0, var passingPeriodLength: Int = 0, var firstPeriodStartTime: String = ""): Parcelable