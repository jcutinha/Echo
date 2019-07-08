package com.btjoe.echo

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable

class Songs(
        var trackID : Long,
        var trackName : String,
        var artistName : String,
        var trackData : String,
        var dateAdded : Long
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readLong()) {
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeLong(trackID)
        dest?.writeString(trackName)
        dest?.writeString(artistName)
        dest?.writeString(trackData)
        dest?.writeLong(dateAdded)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Songs> {
        override fun createFromParcel(parcel: Parcel): Songs {
            return Songs(parcel)
        }

        override fun newArray(size: Int): Array<Songs?> {
            return arrayOfNulls(size)
        }
    }


}