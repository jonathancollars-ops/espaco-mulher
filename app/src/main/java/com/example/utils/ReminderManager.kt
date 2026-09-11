package com.example.utils

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object ReminderManager {
    private const val CHANNEL_ID = "reminders_channel"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Lembretes de Pacientes"
            val descriptionText = "Notificações para datas de reavaliação e agendamentos"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun scheduleReminder(context: Context, patientId: Long, patientName: String, dateString: String) {
        try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = sdf.parse(dateString) ?: return

            val calendar = Calendar.getInstance()
            calendar.time = date
            // Notificar as 08:00 AM do dia agendado
            calendar.set(Calendar.HOUR_OF_DAY, 8)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)

            // Se a data já passou hoje (antes das 8), tenta agendar para o futuro ou ignora
            if (calendar.timeInMillis <= System.currentTimeMillis()) {
                return
            }

            val intent = Intent(context, ReminderReceiver::class.java).apply {
                putExtra("PATIENT_NAME", patientName)
                putExtra("MESSAGE", "Reavaliação / Agendamento programado para hoje.")
            }

            val requestCode = patientId.toInt()
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                }
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            }
            Log.d("ReminderManager", "Scheduled reminder for $patientName on $dateString")
        } catch (e: Exception) {
            Log.e("ReminderManager", "Error scheduling reminder", e)
        }
    }
}
