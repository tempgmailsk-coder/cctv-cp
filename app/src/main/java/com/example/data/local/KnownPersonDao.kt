package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.KnownPerson
import kotlinx.coroutines.flow.Flow

@Dao
interface KnownPersonDao {
    @Query("SELECT * FROM known_persons ORDER BY name ASC")
    fun getAllPersons(): Flow<List<KnownPerson>>

    @Query("SELECT * FROM known_persons WHERE id = :id LIMIT 1")
    suspend fun getPersonById(id: Long): KnownPerson?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerson(person: KnownPerson): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersons(persons: List<KnownPerson>)

    @Update
    suspend fun updatePerson(person: KnownPerson)

    @Delete
    suspend fun deletePerson(person: KnownPerson)

    @Query("DELETE FROM known_persons WHERE id = :id")
    suspend fun deletePersonById(id: Long)
}
