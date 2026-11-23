package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        StudentProfileEntity::class,
        SupplyItemEntity::class,
        ItemReviewEntity::class,
        BorrowRequestEntity::class,
        ChatMessageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class GamiturnDatabase : RoomDatabase() {
    abstract fun gamiturnDao(): GamiturnDao

    companion object {
        @Volatile
        private var INSTANCE: GamiturnDatabase? = null

        fun getInstance(context: Context): GamiturnDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    GamiturnDatabase::class.java,
                    "gamiturn_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
