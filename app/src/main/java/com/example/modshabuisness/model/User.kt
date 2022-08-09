package com.example.modshabuisness.model

import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList

data class User(
    var name:String? = "",
    var mobile:String? = "",
    var email:String? = "",
    var uid:String? = "",
    var wishlist:List<String>? = emptyList(),
    var location:String?=null
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(mobile)
        parcel.writeString(email)
        parcel.writeString(uid)
        parcel.writeStringList(wishlist)
        parcel.writeString(location)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}