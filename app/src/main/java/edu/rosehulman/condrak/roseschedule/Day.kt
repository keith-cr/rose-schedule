package edu.rosehulman.condrak.roseschedule

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Day (var name: String, var periods: List<ClassPeriod>) : Parcelable