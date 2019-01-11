package edu.rosehulman.condrak.roseschedule

import android.os.Parcel
import android.os.Parcelable

data class Schedule (var scheduleSettings: ScheduleSettings, var days: List<Day>): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(ScheduleSettings::class.java.classLoader),
        parcel.createTypedArrayList(Day)
    )
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(scheduleSettings, flags)
        parcel.writeTypedList(days)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Schedule> {
        override fun createFromParcel(parcel: Parcel): Schedule {
            return Schedule(parcel)
        }

        override fun newArray(size: Int): Array<Schedule?> {
            return arrayOfNulls(size)
        }
    }
}