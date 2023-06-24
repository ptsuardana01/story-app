package com.example.storyapp.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.storyapp.data.local.entity.RemoteKey
import com.example.storyapp.data.remote.responses.ListStoryItem

@Database(
    entities = [ListStoryItem::class, RemoteKey::class],
    version = 1,
    exportSchema = false
)
abstract class StoryDatabase : RoomDatabase(){

    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeyDao

    companion object {
        @Volatile
        private var INSTANCE: StoryDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): StoryDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    StoryDatabase::class.java, "db_storyapp"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}