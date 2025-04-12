package com.example.zingmp3clone.data.di

import android.content.Context
import com.example.zingmp3clone.data.local.AppDatabase
import com.example.zingmp3clone.data.local.RecentSongDao
import com.example.zingmp3clone.data.local.SongDao
import com.example.zingmp3clone.data.repository.RecentSongRepository
import com.example.zingmp3clone.data.repository.SongRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase = AppDatabase.getDatabase(context)

    @Provides
    fun provideSongDao(database: AppDatabase): SongDao = database.songDao()

    @Provides
    fun provideRecentSong(database: AppDatabase): RecentSongDao = database.recentSongDao()

    @Provides
    @Singleton
    fun provideSongRepository(songDao: SongDao): SongRepository = SongRepository(songDao)

    @Provides
    @Singleton
    fun provideRecentSongRepository(songDao: SongDao, recentSongDao: RecentSongDao): RecentSongRepository = RecentSongRepository(songDao, recentSongDao)
}