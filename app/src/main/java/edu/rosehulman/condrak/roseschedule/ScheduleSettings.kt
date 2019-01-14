package edu.rosehulman.condrak.roseschedule

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.joda.time.LocalTime

@Parcelize
data class ScheduleSettings (var periodLength: Int, var passingPeriodLength: Int, var firstPeriodStartTime: LocalTime): Parcelable