package com.flownote.di

import android.content.Context
import androidx.room.Room
import com.flownote.data.local.dao.NoteDao
import com.flownote.data.local.database.FlowNoteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for database dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    /**
     * Provide Room database instance
     */
    @Provides
    @Singleton
    fun provideFlowNoteDatabase(
        @ApplicationContext context: Context
    ): FlowNoteDatabase {
        return Room.databaseBuilder(
            context,
            FlowNoteDatabase::class.java,
            FlowNoteDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration() // For development; remove in production
            .build()
    }
    
    /**
     * Provide NoteDao
     */
    @Provides
    @Singleton
    fun provideNoteDao(database: FlowNoteDatabase): NoteDao {
        return database.noteDao()
    }
}
