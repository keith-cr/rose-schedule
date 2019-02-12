package edu.rosehulman.condrak.roseschedule

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.app.NotificationCompat
import android.support.v7.app.AppCompatActivity
import com.google.gson.Gson


class NotificationPublisher : BroadcastReceiver() {

    //TODO: Implement with Firebase to confirm period still has notifications/alarms enabled

    //TODO: Register notifications/alarms on device boot

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        val bundle = intent.getBundleExtra("BUNDLE")
        val notificationInfo = bundle.getParcelable<NotificationInfo>(NOTIFICATION_INFO)!!
        notificationManager.notify(notificationInfo.notificationId, getNotification(notificationInfo.period,
            notificationInfo.scheduleTiming, Constants.CLASS_NOTIFICATIONS_ID, context))
        val period = notificationInfo.period
        val dayConstant = notificationInfo.day
        val scheduleTiming = notificationInfo.scheduleTiming
        val delay: Long = scheduleTiming.getNotificationDelay(dayConstant, period, true)
        scheduleNotification(NotificationInfo(dayConstant, period, scheduleTiming, 1), delay, context)
    }

    private fun getNotification(period: ClassPeriod, scheduleTiming: ScheduleTiming, channelID: String,
                                context: Context): Notification {
        val builder = NotificationCompat.Builder(context, channelID)
        builder.setContentTitle(context.resources.getString(R.string.notification_title, period.className,
            period.minutesBefore.toString()))
        builder.setContentText(if (period.hasLocation())
            context.resources.getString(R.string.notification_text_with_location,
                period.className, period.classLocation, scheduleTiming.getStartTime(period).toString("h:mm a"))
        else
            context.resources.getString(R.string.notification_text,
                period.className, scheduleTiming.getStartTime(period).toString("h:mm a")))
        builder.setSmallIcon(R.drawable.ic_logo)
        builder.setChannelId(channelID)
        return builder.build()
    }

    private fun scheduleNotification(notificationInfo: NotificationInfo, delay: Long, context: Context) {
        val bundle = Bundle()
        bundle.putParcelable(NotificationPublisher.NOTIFICATION_INFO, notificationInfo)
        val notificationIntent = Intent(context.applicationContext, NotificationPublisher::class.java)
        notificationIntent.putExtra("BUNDLE", bundle)
        val requestCode = System.currentTimeMillis().toInt()
        val pendingIntent = PendingIntent.getBroadcast(context.applicationContext, requestCode,
            notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val futureInMillis = SystemClock.elapsedRealtime() + delay
        val alarmManager = context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent)

        val prefs = context.getSharedPreferences(Constants.PREFS, AppCompatActivity.MODE_PRIVATE)
        val requestCodesWrapperString = prefs.getString(Constants.KEY_PENDINGINTENTS, null)
        val requestCodesWrapper = if (requestCodesWrapperString != null)
            Gson().fromJson(requestCodesWrapperString, RequestCodesWrapper::class.java)
        else
            RequestCodesWrapper(ArrayList())
        requestCodesWrapper.requestCodes.add(requestCode)
        val editor = prefs.edit()
        editor.putString(Constants.KEY_PENDINGINTENTS, Gson().toJson(requestCodesWrapper))
        editor.apply()
    }

    companion object {
        var NOTIFICATION_INFO = "notificationInfo"
    }
}
