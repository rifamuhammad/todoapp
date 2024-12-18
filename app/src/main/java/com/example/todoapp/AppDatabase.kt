package com.example.todoapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [TodoModel::class], version = 3) // Updated version to 2
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao

    companion object {
        // Singleton prevents multiple instances of database opening at the same time.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    // Add migration
                    .addMigrations(MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                return instance
            }
        }

        // Migration from version 1 to version 2
        // Migration from version 2 to version 3
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create a new table with the updated schema
                database.execSQL("""
            CREATE TABLE new_TodoModel (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                description TEXT NOT NULL,
                category TEXT NOT NULL,
                date INTEGER NOT NULL,
                time INTEGER NOT NULL,
                tasks TEXT NOT NULL DEFAULT '',
                checkboxes TEXT NOT NULL DEFAULT '',  
                isFinished INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent())

                // Copy the data from the old table to the new table
                database.execSQL("""
            INSERT INTO new_TodoModel (id, title, description, category, date, time, tasks, isFinished)
            SELECT id, title, description, category, date, time, tasks, isFinished FROM TodoModel
        """)

                // Drop the old table
                database.execSQL("DROP TABLE TodoModel")

                // Rename the new table to the old table name
                database.execSQL("ALTER TABLE new_TodoModel RENAME TO TodoModel")
            }
        }


    }
}
