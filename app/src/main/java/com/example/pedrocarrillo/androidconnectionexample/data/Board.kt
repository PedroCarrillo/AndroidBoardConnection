package com.example.pedrocarrillo.androidconnectionexample.data

import android.os.Parcel
import android.os.Parcelable

/**
 * @author Pedro Carrillo
 */
class Board(val ip : String, val port : Int) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readInt())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(ip)
        parcel.writeInt(port)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Board> {
        override fun createFromParcel(parcel: Parcel): Board {
            return Board(parcel)
        }

        override fun newArray(size: Int): Array<Board?> {
            return arrayOfNulls(size)
        }
    }
}