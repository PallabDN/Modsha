package com.example.modshabuisness.model

import android.os.Parcel
import android.os.Parcelable

data class TrendingProduct(
    val trending:List<String>? = emptyList()
) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.createStringArrayList()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeStringList(trending)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TrendingProduct> {
        override fun createFromParcel(parcel: Parcel): TrendingProduct {
            return TrendingProduct(parcel)
        }

        override fun newArray(size: Int): Array<TrendingProduct?> {
            return arrayOfNulls(size)
        }
    }
}