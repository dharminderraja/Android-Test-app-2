package com.example.yumemitest

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "todo")
data class Todo(
    @PrimaryKey (autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "content") var content: String?,
    @ColumnInfo(name = "status") var status: Boolean?
)