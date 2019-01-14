package edu.rosehulman.condrak.roseschedule

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.joda.time.LocalTime

@Parcelize
data class Schedule (var scheduleSettings: ScheduleSettings, var days: List<Day>): Parcelable {

    companion object {
        fun createEmptySchedule(scheduleSettings: ScheduleSettings): Schedule {
            val freePeriods: ArrayList<ClassPeriod> = ArrayList()
            for (i in 1..10)
                freePeriods.add(ClassPeriod(i,"Free", ""))
            val days: ArrayList<Day> = ArrayList()
            days.add(Day("Monday", freePeriods))
            return Schedule(scheduleSettings, days)
        }
    }
}