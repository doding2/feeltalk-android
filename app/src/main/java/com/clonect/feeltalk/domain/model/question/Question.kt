package com.clonect.feeltalk.domain.model.question

import android.os.Parcel
import android.os.Parcelable

data class Question(
    val id: Long = 0,
    val contentPrefix: String = "",
    val content: String = "",
    val contentSuffix: String = "",
    var myAnswer: String = "",
    val partnerAnswer: String = "",
    val myAnswerDate: String = "",
    val partnerAnswerDate: String =""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Long::class.java.classLoader) as Long,
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "") {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(contentPrefix)
        parcel.writeString(content)
        parcel.writeString(contentSuffix)
        parcel.writeString(myAnswer)
        parcel.writeString(partnerAnswer)
        parcel.writeString(myAnswerDate)
        parcel.writeString(partnerAnswerDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Question> {
        override fun createFromParcel(parcel: Parcel): Question {
            return Question(parcel)
        }

        override fun newArray(size: Int): Array<Question?> {
            return arrayOfNulls(size)
        }
    }

}
