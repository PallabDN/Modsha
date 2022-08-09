package com.example.modshabuisness.model

import android.os.Parcel
import android.os.Parcelable

data class UserProductModel(
    val productId:String? = "",
    val quantity:Int = 0,
    val size:String? = "",
    val status:Int = 0
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(productId)
        parcel.writeInt(quantity)
        parcel.writeString(size)
        parcel.writeInt(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserProductModel> {
        override fun createFromParcel(parcel: Parcel): UserProductModel {
            return UserProductModel(parcel)
        }

        override fun newArray(size: Int): Array<UserProductModel?> {
            return arrayOfNulls(size)
        }
    }
}