package edu.rosehulman.condrak.roseschedule

import android.os.Parcel
import android.os.Parcelable

data class Day (var name: String, var periods: List<ClassPeriod>) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.createTypedArrayList(ClassPeriod)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeTypedList(periods)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Day> {
        override fun createFromParcel(parcel: Parcel): Day {
            return Day(parcel)
        }

        override fun newArray(size: Int): Array<Day?> {
            return arrayOfNulls(size)
        }
    }

}