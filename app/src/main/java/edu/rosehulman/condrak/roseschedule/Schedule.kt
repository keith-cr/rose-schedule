package edu.rosehulman.condrak.roseschedule

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Schedule (var scheduleSettings: ScheduleSettings = ScheduleSettings(), var days: MutableList<Day> = ArrayList()): Parcelable {

    companion object {
        fun createEmptySchedule(scheduleSettings: ScheduleSettings): Schedule {
            val freePeriods: ArrayList<ClassPeriod> = ArrayList()
            for (i in 1..10)
                freePeriods.add(ClassPeriod(i))
            val days: ArrayList<Day> = ArrayList()
            days.add(Day("Monday", freePeriods))
            days.add(Day("Tuesday", freePeriods))
            days.add(Day("Wednesday", freePeriods))
            days.add(Day("Thursday", freePeriods))
            days.add(Day("Friday", freePeriods))
            return Schedule(scheduleSettings, days)
        }
    }
}