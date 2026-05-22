package fr.taoufikcode.data.smartphones.local

import androidx.room.Database
import androidx.room.RoomDatabase
import fr.taoufikcode.data.smartphones.local.dao.HomeDao
import fr.taoufikcode.data.smartphones.local.entity.SmartphoneSummaryEntity

@Database(
    entities = [SmartphoneSummaryEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class SmartphoneDatabase : RoomDatabase() {
    abstract fun homeListDao(): HomeDao
}
