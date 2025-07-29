package com.miabue.app.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.miabue.app.models.Treatment;
import com.miabue.app.NotificationReceiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NotificationHelper {
    private static final String TAG = "NotificationHelper";

    public static void scheduleNotification(Context context, Treatment treatment) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("medication_name", treatment.getName());
        intent.putExtra("dose", treatment.getDose());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                treatment.getId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Calcular próxima notificación
        Calendar calendar = getNextNotificationTime(treatment);

        if (calendar != null) {
            try {
                alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        treatment.getFrequencyHours() * 60 * 60 * 1000L, // Convertir horas a milisegundos
                        pendingIntent
                );
                Log.d(TAG, "Notificación programada para: " + calendar.getTime().toString());
            } catch (Exception e) {
                Log.e(TAG, "Error al programar notificación", e);
            }
        }
    }

    private static Calendar getNextNotificationTime(Treatment treatment) {
        try {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date startTime = timeFormat.parse(treatment.getStartTime());

            Calendar calendar = Calendar.getInstance();
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(startTime);

            // Establecer la hora y minuto del tratamiento
            calendar.set(Calendar.HOUR_OF_DAY, startCalendar.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, startCalendar.get(Calendar.MINUTE));
            calendar.set(Calendar.SECOND, 0);

            // Si la hora ya pasó hoy, programar para mañana
            if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            return calendar;
        } catch (ParseException e) {
            Log.e(TAG, "Error al parsear la hora del tratamiento", e);
            return null;
        }
    }

    public static void cancelNotification(Context context, int treatmentId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                treatmentId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.cancel(pendingIntent);
    }
}
