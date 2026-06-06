package com.example.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlin.random.Random
import com.example.MainActivity

class NotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val messages = listOf(
        "Adventure Time seni bekliyor! Büyülü ormana geri dönmeye ne dersin?",
        "Gumball ile macera zamanı! Darwin seni çok özledi.",
        "Kral Şakir yeni bölümüyle MoonToon'da! Hemen izle!",
        "Ben 10'den yeni dönüşümler! Dünyayı kurtarma sırası sende.",
        "Powerpuff Girls ile aksiyon başlasın! Blossom, Bubbles ve Buttercup seni çağırıyor.",
        "Sıkıldın mı? Düzenli Dizi (Regular Show) ile kafanı dağıt!",
        "Kafadar Ayılar ile bir dilim pizza yemeye var mısın?",
        "MoonToon dünyasına geri dön! Yepyeni çizgi diziler seni bekliyor."
    )

    override suspend fun doWork(): Result {
        showNotification()
        return Result.success()
    }

    private fun showNotification() {
        val message = messages[Random.nextInt(messages.size)]
        val channelId = "moontoon_channel"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "MoonToon Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("MoonToon")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(Random.nextInt(1000), notification)
    }
}
