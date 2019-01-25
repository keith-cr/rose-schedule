package edu.rosehulman.condrak.roseschedule

import org.joda.time.LocalTime

object Constants {
    const val TAG = "ROSE-SCHEDULE"
    const val SCHEDULE_REF = "schedule"
    // FOR TESTING
    val dummyScheduleSettings = ScheduleSettings(50,5, LocalTime("08:05"))
    val dummySchedule = Schedule.createDummySchedule(dummyScheduleSettings)
    val dummyScheduleTiming = ScheduleTiming(dummyScheduleSettings)
}