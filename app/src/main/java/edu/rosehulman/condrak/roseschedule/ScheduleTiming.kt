package edu.rosehulman.condrak.roseschedule

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.joda.time.*
import org.joda.time.format.PeriodFormatterBuilder

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

    fun isNow(classPeriod: ClassPeriod): Boolean {
        return getStartTime(classPeriod).minusMinutes(scheduleSettings.passingPeriodLength).isBefore(LocalTime()) && getEndTime(classPeriod).isAfter(LocalTime())
    }

    private fun currentPeriod(day: Day): Int {
        for (period in day.periods) {
            if (isNow(period))
                return period.periodNumber
        }
        return if (getStartTime(ClassPeriod(10,"","")).isBefore(LocalTime()))
            -2
        else
            -1
    }

    fun getCurrentClass(schedule: Schedule): ClassPeriod? {
        val day = getCurrentClassDay()
        if (LocalDate().dayOfWeek in 1..5) {
            val currentPeriod = currentPeriod(schedule.days[day])
            return when (currentPeriod) {
                -1 -> {
                    return null
                }
                -2 -> {
                    return null
                }
                else -> {
                    schedule.days[day].periods[currentPeriod]
                }
            }
        } else {
            return null
        }
    }

    fun getNextClass(schedule: Schedule): ClassPeriod {
        if (LocalDate().dayOfWeek in 1..5) {
            var day = LocalDate().dayOfWeek
            val currentPeriod = currentPeriod(schedule.days[day-1])
            return when (currentPeriod) {
                -1 -> {
                    val periodsWithoutFrees = schedule.days[day-1].periods.toMutableList()
                    periodsWithoutFrees.removeAll { it.isFree }
                    periodsWithoutFrees[0]
                }
                -2 -> {
                    day += 1
                    if (day > 4)
                        day = 1
                    val periodsWithoutFrees = schedule.days[day].periods.toMutableList()
                    periodsWithoutFrees.removeAll { it.isFree }
                    periodsWithoutFrees[0]
                }
                else -> {
                    schedule.days[day].periods[currentPeriod]
                }
            }
        } else {
            val periodsWithoutFrees = schedule.days[0].periods.toMutableList()
            periodsWithoutFrees.removeAll { it.isFree }
            return periodsWithoutFrees.first()
        }
    }

    fun getCurrentClassDay(): Int {
        var day = LocalDate().dayOfWeek
        day = if (day in 1..5) {
            day
        } else {
            1
        }
        day -= 1
        return day
    }

    fun getRelativeStartTime(period: ClassPeriod): String {
        val now = DateTime()
        val timePeriod = if (LocalDateTime().dayOfWeek in 1..5) {
            Period(now, getStartTime(period).toDateTimeToday())
        } else {
            Period(now, getStartTime(period).toDateTimeToday().plusDays(1))
        }

        val formatter = PeriodFormatterBuilder()
            .appendDays().appendSuffix(" day", " days")
            .appendSeparator(" and ")
            .appendHours().appendSuffix("hour", " hours")
            .appendSeparator(" and ")
            .appendMinutes().appendSuffix(" minute", " minutes")
            .printZeroNever()
            .toFormatter()

        return formatter.print(timePeriod)
    }

    fun getRelativeEndTime(period: ClassPeriod): String {
        val now = DateTime()
        val timePeriod = Period(now, getEndTime(period).toDateTime(Instant(DateTime().plusDays(1))))

        val formatter = PeriodFormatterBuilder()
            .appendMinutes().appendSuffix(" minute", " minutes")
            .printZeroNever()
            .toFormatter()

        return formatter.print(timePeriod)
    }
}