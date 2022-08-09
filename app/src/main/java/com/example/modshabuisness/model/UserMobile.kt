package com.example.modshabuisness.model

import android.os.Parcel
import android.os.Parcelable

data class UserMobile (
    val user_mobile:List<String>? = emptyList()
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.createStringArrayList()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeStringList(user_mobile)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserMobile> {
        override fun createFromParcel(parcel: Parcel): UserMobile {
            return UserMobile(parcel)
        }

        override fun newArray(size: Int): Array<UserMobile?> {
            return arrayOfNulls(size)
        }
    }
}