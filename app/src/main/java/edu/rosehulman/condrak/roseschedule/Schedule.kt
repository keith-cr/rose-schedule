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

        fun createDummySchedule(scheduleSettings: ScheduleSettings): Schedule {
            val mRF: ArrayList<ClassPeriod> = ArrayList()
            val t: ArrayList<ClassPeriod> = ArrayList()
            val w: ArrayList<ClassPeriod> = ArrayList()
            mRF.add(ClassPeriod(mRF.size+1, "MA112","G221"))
            mRF.add(ClassPeriod(mRF.size+1))
            mRF.add(ClassPeriod(mRF.size+1, "PH112","O229"))
            mRF.add(ClassPeriod(mRF.size+1))
            mRF.add(ClassPeriod(mRF.size+1))
            mRF.add(ClassPeriod(mRF.size+1))
            mRF.add(ClassPeriod(mRF.size+1))
            mRF.add(ClassPeriod(mRF.size+1, "SV322","O231"))
            mRF.add(ClassPeriod(mRF.size+1))
            mRF.add(ClassPeriod(mRF.size+1, "CSSE483","O259"))
            t.add(ClassPeriod(t.size+1, "MA112","G221"))
            t.add(ClassPeriod(t.size+1))
            t.add(ClassPeriod(t.size+1, "PH112","O229"))
            t.add(ClassPeriod(t.size+1, "PH112L","BL114"))
            t.add(ClassPeriod(t.size+1, "PH112L","BL114"))
            t.add(ClassPeriod(t.size+1, "PH112L","BL114"))
            t.add(ClassPeriod(t.size+1))
            t.add(ClassPeriod(t.size+1, "SV322","O231"))
            t.add(ClassPeriod(t.size+1))
            t.add(ClassPeriod(t.size+1, "CSSE483","O259"))
            w.add(ClassPeriod(w.size+1, "MA112","G221"))
            for (i in 2..10)
                w.add(ClassPeriod(i))
            val days: ArrayList<Day> = ArrayList()
            days.add(Day("Monday", mRF))
            days.add(Day("Tuesday", t))
            days.add(Day("Wednesday", w))
            days.add(Day("Thursday", mRF))
            days.add(Day("Friday", mRF))
            return Schedule(scheduleSettings, days)
        }
    }
}