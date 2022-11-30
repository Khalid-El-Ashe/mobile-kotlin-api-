package com.example.kotlinrestapi.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.kotlinrestapi.models.Article

@Database(entities = [Article::class], version = 1)
@TypeConverters(Converters::class) // this is to use converter Source class
abstract class ArticleDatabase : RoomDatabase() {

    abstract fun getArticleDao(): ArticleDao

    companion object {
        @Volatile // this is to can other threads can immediately see when a thread changes this instance
        private var instance: ArticleDatabase? = null

        // i need to make synchronize that instance ( create instance for database )
        private val LOCK = Any()

        // i need to make this function to coll when make initialize object of ArticleDatabase
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also { instance = it }
        }

        // this function to create the database in device
        private fun createDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            ArticleDatabase::class.java,
            "article_database.db"
        ).build()
    }
}