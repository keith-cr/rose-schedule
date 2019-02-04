package edu.rosehulman.condrak.roseschedule

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Day (var name: String = "", var periods: MutableList<ClassPeriod> = ArrayList()) : Parcelable