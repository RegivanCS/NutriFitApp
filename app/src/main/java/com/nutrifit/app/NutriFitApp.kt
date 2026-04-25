package com.nutrifit.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NutriFitApp : Application() {

    companion object {
        const val CHANNEL_ID_WATER = "water_reminder"
        const val CHANNEL_ID_MEAL = "meal_reminders"
        const val CHANNEL_ID_EXERCISE = "exercise_reminders"
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
            val alarmeSom = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            // Canal para lembretes de refeição (com som de ALARME + vibração)
            val mealChannel = NotificationChannel(
                CHANNEL_ID_MEAL,
                "Lembretes de Refeições",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Lembretes na hora das refeições com som e vibração"
                enableVibration(true)
                setSound(alarmeSom, audioAttributes)
                enableLights(true)
            }

            // Canal para lembretes de exercício
            val exerciseChannel = NotificationChannel(
                CHANNEL_ID_EXERCISE,
                "Lembretes de Exercícios",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Lembretes para fazer exercícios"
                enableVibration(true)
                setSound(alarmeSom, audioAttributes)
            }

            // Canal para lembrete de água
            val waterChannel = NotificationChannel(
                CHANNEL_ID_WATER,
                "Lembrete de Água",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Lembretes para beber água ao longo do dia"
                enableVibration(true)
            }

            // Canal para motivação diária (mais sutil)
            val motivationChannel = NotificationChannel(
                CHANNEL_ID_MOTIVATION,
                "Motivação Diária",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Mensagens motivacionais diárias"
                setSound(null, null) // Sem som
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(mealChannel)
            notificationManager.createNotificationChannel(exerciseChannel)
            notificationManager.createNotificationChannel(waterChannel)
            notificationManager.createNotificationChannel(motivationChannel)
        }
    }
}