package com.getpet.Model.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey()
    val id: String,
    val img: String,
    val kind: String,
    val age: String,
    val about: String,
    val phone: String,
    val location: String,
    val owner: String,
    val uid: String
)

