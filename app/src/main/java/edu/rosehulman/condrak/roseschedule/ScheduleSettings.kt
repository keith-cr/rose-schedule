package edu.rosehulman.condrak.roseschedule

import android.os.Parcel
import android.os.Parcelable

data class ScheduleSettings (var periodLength: Int, var passingPeriodLength: Int, var firstPeriodTimeSet: Float): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readFloat()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(periodLength)
        parcel.writeInt(passingPeriodLength)
        parcel.writeFloat(firstPeriodTimeSet)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ScheduleSettings> {
        override fun createFromParcel(parcel: Parcel): ScheduleSettings {
            return ScheduleSettings(parcel)
        }

        override fun newArray(size: Int): Array<ScheduleSettings?> {
            return arrayOfNulls(size)
        }
    }
}