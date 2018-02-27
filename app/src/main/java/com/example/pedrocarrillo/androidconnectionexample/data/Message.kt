package com.example.pedrocarrillo.androidconnectionexample.data

import android.os.Parcel
import android.os.Parcelable

/**
 * @author Pedro Carrillo.
 */
data class Message(val id : String, val data : ByteArray) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.createByteArray())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeByteArray(data)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Message> {
        override fun createFromParcel(parcel: Parcel): Message {
            return Message(parcel)
        }

        override fun newArray(size: Int): Array<Message?> {
            return arrayOfNulls(size)
        }
    }
}