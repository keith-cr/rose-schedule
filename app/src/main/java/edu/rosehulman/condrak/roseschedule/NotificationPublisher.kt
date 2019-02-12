package edu.rosehulman.condrak.roseschedule

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.app.NotificationCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

// Notification code modified from: https://gist.github.com/BrandonSmith/6679223
class NotificationPublisher : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val bundle = intent.getBundleExtra(Constants.KEY_BUNDLE)
        val notificationInfo = bundle.getParcelable<NotificationInfo>(NOTIFICATION_INFO)!!
        val prefs = context.getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE)
        val uid = prefs.getString(Constants.KEY_UID, "")!!
        FirebaseFirestore
            .getInstance()
            .collection(Constants.USERS_COLLECTION)
            .document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val schedule = document.toObject(Schedule::class.java)!!
                    val scheduleTiming = ScheduleTiming(schedule.scheduleSettings)
                    val period = schedule.days[notificationInfo.dayIndex]
                        .periods[notificationInfo.periodIndex]
                    if (period.hasAlarm || period.hasNotification) {
                        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                                as NotificationManager
                        val id = System.currentTimeMillis().toInt()
                        if (!period.hasAlarm) {
                            notificationManager.notify(
                                id, getNotification(
                                    period, scheduleTiming, id,
                                    Constants.CLASS_NOTIFICATIONS_CHANNEL_ID, context
                                )
                            )
                        } else {
                            notificationManager.notify(
                                id, getNotification(
                                    period, scheduleTiming, id,
                                    Constants.CLASS_ALARMS_CHANNEL_ID, context
                                )
                            )
                        }
                        val dayConstant = notificationInfo.day
                        val delay: Long = scheduleTiming.getNotificationDelay(dayConstant, period, true)
                        scheduleNotification(notificationInfo, delay, context)
                    }
                } else {
                    Log.wtf(Constants.TAG, "Couldn't find users schedule")
                }
            }
            .addOnFailureListener { exception ->
                Log.e(Constants.TAG, "get failed with ", exception)
            }
    }

    private fun getNotification(period: ClassPeriod, scheduleTiming: ScheduleTiming,
                                notificationId: Int, notificationGroupId: String, context: Context): Notification {
        val builder = NotificationCompat.Builder(context, notificationGroupId)
            .setContentTitle(context.resources.getString(R.string.notification_title, period.className,
            period.minutesBefore.toString()))
            .setContentText(if (period.hasLocation())
                context.resources.getString(R.string.notification_text_with_location,
                period.className, period.classLocation, scheduleTiming.getStartTime(period).toString("h:mm a"))
            else
                context.resources.getString(R.string.notification_text,
                period.className, scheduleTiming.getStartTime(period).toString("h:mm a")))
            .setSmallIcon(R.drawable.ic_logo)
            .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
            .setChannelId(notificationGroupId)
            .setColor(ResourcesCompat.getColor(context.resources, R.color.colorPrimary, null))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
        if (period.hasAlarm) {
            val alarm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            if (AlarmDismisser.ringtone == null)
                AlarmDismisser.ringtone = RingtoneManager.getRingtone(context.applicationContext, alarm)
            val r = AlarmDismisser.ringtone!!
            r.play()
            val bundle = Bundle()
            bundle.putInt(Constants.KEY_NOTIFICATION_ID, notificationId)
            val dismissIntent = Intent(context.applicationContext, AlarmDismisser::class.java)
            dismissIntent.putExtra(Constants.KEY_BUNDLE, bundle)
            val requestCode = System.currentTimeMillis().toInt()
            val dismissPendingIntent: PendingIntent =
                PendingIntent.getBroadcast(context.applicationContext, requestCode, dismissIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT)
            builder.addAction(R.drawable.ic_dismiss, context.getString(R.string.dismiss),
                dismissPendingIntent)
            builder.setOngoing(true)
            builder.setSound(null)
        } else {
            builder.setDefaults(NotificationCompat.DEFAULT_ALL)
        }
        return builder.build()
    }

    private fun scheduleNotification(notificationInfo: NotificationInfo, delay: Long, context: Context) {
        val bundle = Bundle()
        bundle.putParcelable(NotificationPublisher.NOTIFICATION_INFO, notificationInfo)
        val notificationIntent = Intent(context.applicationContext, NotificationPublisher::class.java)
        notificationIntent.putExtra(Constants.KEY_BUNDLE, bundle)
        val requestCode = System.currentTimeMillis().toInt()
        val pendingIntent = PendingIntent.getBroadcast(context.applicationContext, requestCode,
            notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val futureInMillis = SystemClock.elapsedRealtime() + delay
        val alarmManager = context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent)

        val prefs = context.getSharedPreferences(Constants.PREFS, AppCompatActivity.MODE_PRIVATE)
        val requestCodesWrapperString = prefs.getString(Constants.KEY_REQUEST_CODES, null)
        val requestCodesWrapper = if (requestCodesWrapperString != null)
            Gson().fromJson(requestCodesWrapperString, RequestCodesWrapper::class.java)
        else
            RequestCodesWrapper(ArrayList())
        requestCodesWrapper.requestCodes.add(requestCode)
        val editor = prefs.edit()
        editor.putString(Constants.KEY_REQUEST_CODES, Gson().toJson(requestCodesWrapper))
        editor.apply()
    }

    companion object {
        var NOTIFICATION_INFO = "notificationInfo"
    }
}
