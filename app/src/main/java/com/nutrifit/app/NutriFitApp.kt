package com.nutrifit.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NutriFitApp : Application() {

    companion object {
        const val CHANNEL_ID_WATER = "water_reminder"
        const val CHANNEL_ID_MEAL = "meal_reminder"
        const val CHANNEL_ID_MOTIVATION = "daily_motivation"
        lateinit var instance: NutriFitApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Canal para lembrete de água
            val waterChannel = NotificationChannel(
                CHANNEL_ID_WATER,
                "Lembrete de Água",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Lembretes para beber água ao longo do dia"
            }

            // Canal para lembrete de refeições
            val mealChannel = NotificationChannel(
                CHANNEL_ID_MEAL,
                "Lembretes de Refeições",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Lembretes na hora das refeições"
            }

            // Canal para motivação diária
            val motivationChannel = NotificationChannel(
                CHANNEL_ID_MOTIVATION,
                "Motivação Diária",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Mensagens motivacionais diárias"
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(waterChannel)
            notificationManager.createNotificationChannel(mealChannel)
            notificationManager.createNotificationChannel(motivationChannel)
        }
    }
}
