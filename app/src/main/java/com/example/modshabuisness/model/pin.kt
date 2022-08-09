package com.example.modshabuisness.model

import android.os.Parcel
import android.os.Parcelable

data class pin(
    val pin:List<String>? = emptyList()
): Parcelable {
    constructor(parcel: Parcel) : this(parcel.createStringArrayList()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeStringList(pin)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<pin> {
        override fun createFromParcel(parcel: Parcel): pin {
            return pin(parcel)
        }

        override fun newArray(size: Int): Array<pin?> {
            return arrayOfNulls(size)
        }
    }
}



