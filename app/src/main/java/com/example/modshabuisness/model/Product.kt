package com.example.modshabuisness.model

import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList

data class Product(
    var charge: List<String>? = emptyList(),
    var description: String? = "",
    var id: String? = "",
    var image: List<String>? = emptyList(),
    var name: String? = "",
    var priceA: List<String>? = emptyList(),
    var priceD: List<String>? = emptyList(),
    var quantity: List<String>? = emptyList(),
    var size: List<String>? = emptyList(),
    var tag: List<String>? = emptyList()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.createStringArrayList(),
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.createStringArrayList(),
        parcel.createStringArrayList(),
        parcel.createStringArrayList(),
        parcel.createStringArrayList()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeStringList(charge)
        parcel.writeString(description)
        parcel.writeString(id)
        parcel.writeStringList(image)
        parcel.writeString(name)
        parcel.writeStringList(priceA)
        parcel.writeStringList(priceD)
        parcel.writeStringList(quantity)
        parcel.writeStringList(size)
        parcel.writeStringList(tag)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }
}
