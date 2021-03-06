package edu.rosehulman.condrak.roseschedule

import android.content.Context
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import net.danlew.android.joda.JodaTimeAndroid
import org.joda.time.*
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.PeriodFormatterBuilder
import java.util.*


@Parcelize
class ScheduleTiming(private val scheduleSettings: ScheduleSettings) : Parcelable {

    fun init(context: Context) {
        JodaTimeAndroid.init(context)
        DateTimeZone.setDefault(DateTimeZone.forTimeZone(TimeZone.getDefault()))
    }

    fun isEmptySchedule(schedule: Schedule): Boolean {
        for (day in schedule.days) {
            for (period in day.periods) {
                if (!period.isFree)
                    return false
            }
        }
        return true
    }

    fun getStartTime(classPeriod: ClassPeriod): LocalTime {
        val fmt = DateTimeFormat.forPattern("h:mm a")
        return fmt.parseLocalTime(scheduleSettings.firstPeriodStartTime).plusMinutes((classPeriod.periodNumber-1)
                *(scheduleSettings.periodLength+scheduleSettings.passingPeriodLength))
    }

    fun getEndTime(classPeriod: ClassPeriod): LocalTime {
        val fmt = DateTimeFormat.forPattern("h:mm a")
        return fmt.parseLocalTime(scheduleSettings.firstPeriodStartTime).plusMinutes((classPeriod.periodNumber-1)
                *(scheduleSettings.periodLength+scheduleSettings.passingPeriodLength)+scheduleSettings.periodLength)
    }

    fun isNow(classPeriod: ClassPeriod): Boolean {
        return getStartTime(classPeriod).minusMinutes(scheduleSettings.passingPeriodLength).isBefore(LocalTime()) &&
                getEndTime(classPeriod).isAfter(LocalTime())
    }

    private fun currentPeriod(day: Day): Int {
        for (period in day.periods) {
            if (isNow(period))
                return period.periodNumber-1
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

    fun getNextClass(schedule: Schedule): ClassPeriod? {
        if (LocalDate().dayOfWeek in 1..5) {
            var day = getCurrentClassDay()
            val currentPeriod = currentPeriod(schedule.days[day])
            return when (currentPeriod) {
                -1 -> {
                    val periodsWithoutFrees = schedule.days[day].periods.toMutableList()
                    periodsWithoutFrees.removeAll { it.isFree }
                    if (!periodsWithoutFrees.isEmpty())
                        periodsWithoutFrees.first()
                    else
                        null
                }
                -2 -> {
                    day += 1
                    if (day > 4)
                        day = 1
                    val periodsWithoutFrees = schedule.days[day].periods.toMutableList()
                    periodsWithoutFrees.removeAll { it.isFree }
                    if (!periodsWithoutFrees.isEmpty())
                        periodsWithoutFrees.first()
                    else
                        null
                }
                else -> {
                    if (schedule.days[day].periods.size == currentPeriod+1)
                        null
                    else
                        schedule.days[day].periods[currentPeriod+1]
                }
            }
        } else {
            val periodsWithoutFrees = schedule.days[0].periods.toMutableList()
            periodsWithoutFrees.removeAll { it.isFree }
            return if (!periodsWithoutFrees.isEmpty())
                periodsWithoutFrees.first()
            else
                null
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

    fun getRelativeStartTime(schedule: Schedule, period: ClassPeriod): String {
        val now = DateTime()
        val timePeriod = when {
            (LocalDateTime().dayOfWeek in 1..4 && currentPeriod(schedule.days[getCurrentClassDay()]) == -2) ->
                Period(now, getStartTime(period).toDateTimeToday().plusDays(1))
            LocalDateTime().dayOfWeek == 5 && currentPeriod(schedule.days[getCurrentClassDay()]) == -2 ->
                Period(now, getStartTime(period).toDateTimeToday().plusDays(3))
            LocalDateTime().dayOfWeek in 1..5 -> Period(now, getStartTime(period).toDateTimeToday())
            LocalDateTime().dayOfWeek == 7 -> Period(now, getStartTime(period).toDateTimeToday().plusDays(1))
            else -> Period(now, getStartTime(period).toDateTimeToday().plusDays(2))
        }

        val formatter = PeriodFormatterBuilder()
            .appendDays().appendSuffix(" day", " days")
            .appendSeparatorIfFieldsBefore(" and ")
            .appendHours().appendSuffix("hour", " hours")
            .appendSeparatorIfFieldsBefore(" and ")
            .appendMinutes().appendSuffix(" minute", " minutes")
            .printZeroNever()
            .toFormatter()

        return formatter.print(timePeriod.plusMinutes(1))
    }

    fun getRelativeEndTime(period: ClassPeriod): String {
        val now = DateTime()
        val timePeriod = Period(now, getEndTime(period).toDateTime(Instant(DateTime().plusDays(1))))

        val formatter = PeriodFormatterBuilder()
            .appendMinutes().appendSuffix(" minute", " minutes")
            .printZeroNever()
            .toFormatter()

        return formatter.print(timePeriod.plusMinutes(1))
    }

    fun getNotificationDelay(day: Int, period: ClassPeriod, forceNext: Boolean): Long {
        val now = DateTime()
        val date = if (now.dayOfWeek == day && !forceNext) {
            if (getStartTime(period).minusMinutes(period.minutesBefore).isBefore(LocalTime()))
                calcNextDay(day)
            else
                LocalDate()
        } else
             calcNextDay(day)
        val dateTime = date.toDateTime(getStartTime(period).minusMinutes(period.minutesBefore))
        val timePeriod = Period(now, dateTime)

        return timePeriod.toStandardDuration().millis
    }

    private fun calcNextDay(day: Int): LocalDate {
        var d = LocalDate()
        if (d.dayOfWeek >= day) {
            d = d.plusWeeks(1)
        }
        return d.withDayOfWeek(day)
    }

}