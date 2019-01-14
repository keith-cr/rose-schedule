package edu.rosehulman.condrak.roseschedule

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.joda.time.LocalTime

@Parcelize
class ScheduleTiming(private val scheduleSettings: ScheduleSettings) : Parcelable {
    fun getStartTime(classPeriod: ClassPeriod): LocalTime {
        return scheduleSettings.firstPeriodStartTime.plusMinutes((classPeriod.periodNumber-1)
                *(scheduleSettings.periodLength+scheduleSettings.passingPeriodLength))
    }

    fun getEndTime(classPeriod: ClassPeriod): LocalTime {
        return scheduleSettings.firstPeriodStartTime.plusMinutes((classPeriod.periodNumber-1)
                *(scheduleSettings.periodLength+scheduleSettings.passingPeriodLength)+scheduleSettings.periodLength)
    }
}