package com.nutrifit.app.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.nutrifit.app.MainActivity

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_MEALS = "meal_reminders"
        const val CHANNEL_EXERCISE = "exercise_reminders"
        const val CHANNEL_MOTIVATION = "daily_motivation"

        private var notificationId = 1000

        private val nomesRefeicoes = mapOf(
            "cafe_da_manha" to "☕ Café da Manhã",
            "lanche_manha" to "🍎 Lanche da Manhã",
            "almoco" to "🍗 Almoço",
            "lanche_tarde" to "🥜 Lanche da Tarde",
            "jantar" to "🥗 Jantar",
            "ceia" to "🌙 Ceia"
        )

        private val mensagensRefeicoes = mapOf(
            "cafe_da_manha" to listOf(
                "Hora do café! ☕ Não pule a refeição mais importante do dia!",
                "Bom dia! Que tal um café da manhã nutritivo hoje? 🥣",
                "Seu corpo agradece um bom café da manhã! 🍳",
                "🌅 Acordou? Hora de nutrir o corpo! Tudo pronto para o café!"
            ),
            "lanche_manha" to listOf(
                "Hora do lanche! 🍎 Mantenha sua energia lá em cima!",
                "Um lanchinho leve para segurar até o almoço! 🥜",
                "Não deixe a fome te pegar! Hora do lanche! ⚡",
                "🚀 Recarregue as energias com um lanche equilibrado!"
            ),
            "almoco" to listOf(
                "Hora do almoço! 🍗 Reabasteça suas energias!",
                "Almoço equilibrado = tarde produtiva! 🥗",
                "Pare tudo! É hora de almoçar! 🍽️",
                "⏰ O meio-dia chegou! Hora de comer bem!"
            ),
            "lanche_tarde" to listOf(
                "Lanche da tarde! 🥜 Aquela energia extra para finalizar o dia!",
                "Não caia na armadilha dos doces! Lanche saudável agora! 🍎",
                "Recarregue as baterias com um lanche leve! ⚡",
                "🌆 Final de tarde pedindo um lanchinho! Hora de comer!"
            ),
            "jantar" to listOf(
                "Hora do jantar! 🥗 Refeição leve para uma boa noite de sono!",
                "Jantar leve hoje? Seu sono agradece! 🌙",
                "Última refeição do dia! Capricho na salada! 🥬",
                "🌃 Dia chegando ao fim! Hora de um jantar leve e nutritivo!"
            ),
            "ceia" to listOf(
                "Ceia leve para dormir bem! 🌙 Nada de exageros!",
                "Hora de encerrar o dia com uma ceia tranquila! 🫖",
                "Chá quente e algo leve para uma boa noite! ☕",
                "🌜 Hora de preparar o corpo para o sono com uma ceia leve!"
            )
        )

        // Recomendações rápidas para mostrar na notificação
        private val dicasRefeicoes = mapOf(
            "cafe_da_manha" to listOf(
                "🥣 Sugestão: Omelete de claras com aveia e banana",
                "🥑 Dica: Torrada integral com abacate e ovo",
                "🥛 Iogurte com granola e frutas vermelhas"
            ),
            "lanche_manha" to listOf(
                "🍎 Maçã com pasta de amendoim e canela",
                "🥤 Iogurte natural com chia e morangos",
                "🥜 Mix de castanhas (10 unidades)"
            ),
            "almoco" to listOf(
                "🥗 Frango grelhado com quinoa e brócolis",
                "🐟 Salmão com batata doce e salada",
                "🥩 Bife magro com arroz integral e legumes"
            ),
            "lanche_tarde" to listOf(
                "🥤 Iogurte com chia e frutas vermelhas",
                "🥪 Sanduíche integral de frango",
                "🥑 Torrada com abacate e ovo"
            ),
            "jantar" to listOf(
                "🥗 Salada completa com atum e ovo",
                "🍲 Sopa de legumes com frango",
                "🥦 Legumes assados com frango grelhado"
            ),
            "ceia" to listOf(
                "🫖 Chá de camomila com canela (leve!)",
                "🥛 Caseína com leite (para ganhar massa!)",
                "💧 Água morna com limão (detox!)"
            )
        )
    }

    /**
     * Mostra notificação de lembrete de refeição com som, vibração e dica do que comer.
     */
    fun showMealReminder(tipo: String, nome: String, objetivo: String = "emagrecer") {
        val nomeRefeicao = nomesRefeicoes[tipo] ?: tipo
        val mensagens = mensagensRefeicoes[tipo] ?: mensagensRefeicoes["cafe_da_manha"]!!
        val dicas = dicasRefeicoes[tipo] ?: dicasRefeicoes["cafe_da_manha"]!!

        val mensagem = mensagens.random()
        val dica = dicas.random()

        // Se tem nome de receita específico da recomendação, usar ele
        val textoCompleto = if (nome.isNotBlank() && nome != tipo) {
            "$mensagem\n\n🍽️ Hora de comer:\n📌 $nome\n💡 $dica"
        } else {
            "$mensagem\n\n💡 $dica"
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmeSom = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(context, CHANNEL_MEALS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("🍽️ $nomeRefeicao - Hora de Comer!")
            .setContentText(mensagem)
            .setStyle(NotificationCompat.BigTextStyle().bigText(textoCompleto))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setSound(alarmeSom)
            .setVibrate(longArrayOf(0, 500, 300, 500, 300, 800, 300, 1000))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId++, notification)
    }

    /**
     * Mostra notificação de lembrete de exercício físico.
     */
    fun showExerciseReminder() {
        val mensagens = listOf(
            "🏃 **Hora do exercício!** 30 minutinhos hoje já fazem diferença!\n\n" +
                "💪 Sugestão:\n• Caminhada rápida (20min)\n• Alongamento (10min)\n• Agachamentos (3x15)",
            "💪 **Mexa-se!** Seu corpo precisa de movimento hoje!\n\n" +
                "🔥 Queima estimada: 150-200 kcal em 30 min de caminhada!",
            "🚶 **Uma caminhada de 20 min** já ajuda a queimar calorias e reduzir o estresse!\n\n" +
                "📊 20 min caminhada = ~100 calorias queimadas!",
            "🏋️ **Bora treinar!** Consistência é a chave do resultado!\n\n" +
                "🎯 Meta do dia: 30 min de atividade física",
            "🧘 **Alongamento agora!** Seu corpo agradece!\n\n" +
                "🙆 5 min de alongamento melhoram postura e circulação!",
            "🔥 **Que tal um cardio rápido hoje?**\n\n" +
                "🏃 Pular corda (10 min) = ~150 kcal!\n🚴 Bicicleta (20 min) = ~200 kcal!"
        )

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_EXERCISE)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("🏋️ Hora do Exercício Físico!")
            .setContentText("30 minutinhos já fazem diferença! ⏰")
            .setStyle(NotificationCompat.BigTextStyle().bigText(mensagens.random()))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId++, notification)
    }
}