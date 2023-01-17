package com.chris.adviceapp.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "advice_table")
data class Advice (
    @ColumnInfo(name = "advice") val advice: String,
    @ColumnInfo(name = "date") val date: String,
//    @ColumnInfo(name = "picture") val imagerUrl: String,
) {
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0
}