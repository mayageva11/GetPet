package com.getpet.Model.Entities

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "posts")
data class PostEntity (
    @PrimaryKey()
    var id: String,
    var img: String,
    var kind: String,
    var age: String, // TODO: Change to birth date
    var about: String,
    var phone: String,
    var location: String,
    var owner: String,
    var uid: String
) : Serializable {

    fun fromMap(map: Map<String?, Any?>) {
        img = map["image"].toString()
        kind = map["kind"].toString()
        age = map["age"].toString()
        about = map["about"].toString()
        phone = map["phone"].toString()
        location = map["location"].toString()
        owner = map["owner"].toString()
        uid = map["uid"].toString()

    }
}

