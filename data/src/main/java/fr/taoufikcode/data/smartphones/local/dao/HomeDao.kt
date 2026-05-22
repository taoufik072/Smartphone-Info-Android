package fr.taoufikcode.data.smartphones.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import fr.taoufikcode.data.smartphones.local.entity.SmartphoneSummaryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeDao {
    @Query("SELECT * FROM smartphones ORDER BY id")
    fun observeHomeItems(): Flow<List<SmartphoneSummaryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<SmartphoneSummaryEntity>)

    @Query("DELETE FROM smartphones")
    suspend fun clear()

    @Transaction
    suspend fun replaceAll(items: List<SmartphoneSummaryEntity>) {
        clear()
        upsertAll(items)
    }
}
