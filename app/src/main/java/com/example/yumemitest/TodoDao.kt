package com.example.yumemitest

import androidx.room.*

@Dao
interface TodoDao {
    @Query("SELECT * FROM todo")
    fun getAll(): MutableList<Todo>

    @Query("SELECT * FROM todo WHERE uid IN (:todoIds)")
    fun loadAllByIds(todoIds: IntArray): List<Todo>

    @Query("SELECT * FROM todo WHERE content LIKE :content")
    fun findByContent(content: String): List<Todo>

    @Insert
    fun insertAll(vararg todo: Todo) : List<Long>

    @Insert
    fun insert(todo: Todo) : Long

    @Update
    fun update(todo: Todo)

    @Update
    fun updatemany(todo: MutableList<Todo>)

    @Delete
    fun delete(todo: Todo)

    @Delete
    fun deletemany(todo: MutableList<Todo>)
}