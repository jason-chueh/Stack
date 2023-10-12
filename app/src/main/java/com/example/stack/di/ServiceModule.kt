package com.example.stack.di

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import androidx.core.app.NotificationCompat
import com.example.stack.MainActivity
import com.example.stack.R
import com.example.stack.service.ACTION_SHOW_WORKOUT_FRAGMENT
import com.example.stack.service.NOTIFICATION_CHANNEL_ID
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule{
    @Provides
    @ServiceScoped
    fun provideMainActivityPendingIntent(
        @ApplicationContext app: Context
    ) = PendingIntent.getActivity(
    app,
    0,
    Intent(app, MainActivity::class.java).also {
        it.action = ACTION_SHOW_WORKOUT_FRAGMENT
//        it.flags = FLAG_ACTIVITY_SINGLE_INSTANCE
    },
    FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    @Provides
    @ServiceScoped
    fun provideBaseNotificationBuilder(
        @ApplicationContext app: Context,
        pendingIntent: PendingIntent
    ) = NotificationCompat.Builder(app, NOTIFICATION_CHANNEL_ID)
    .setAutoCancel(false)
    .setOngoing(true)
    .setSmallIcon(R.drawable.gym)
    .setContentTitle("Stack")
    .setContentText("00:00:00")
    .setContentIntent(pendingIntent)

}