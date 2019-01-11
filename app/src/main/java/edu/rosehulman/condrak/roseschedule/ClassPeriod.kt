package edu.rosehulman.condrak.roseschedule

import android.os.Parcel
import android.os.Parcelable

data class ClassPeriod (var periodNumber: Int, var className: String, var classLocation: String): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(periodNumber)
        parcel.writeString(className)
        parcel.writeString(classLocation)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ClassPeriod> {
        override fun createFromParcel(parcel: Parcel): ClassPeriod {
            return ClassPeriod(parcel)
        }

        override fun newArray(size: Int): Array<ClassPeriod?> {
            return arrayOfNulls(size)
        }
    }
}