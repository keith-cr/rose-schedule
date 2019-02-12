package edu.rosehulman.condrak.roseschedule

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.RemoteViews
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import org.joda.time.DateTimeConstants


class BootReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            val prefs = context.getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE)
            val uid = prefs.getString(Constants.KEY_UID, null)
            if (uid != null) {
                FirebaseFirestore
                    .getInstance()
                    .collection(Constants.USERS_COLLECTION)
                    .document(uid).get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            val schedule = document.toObject(Schedule::class.java)!!
                            val editor = prefs.edit()
                            editor.putString(Constants.KEY_SCHEDULE, Gson().toJson(schedule))
                            editor.apply()
                            val widgetManager = AppWidgetManager.getInstance(context)
                            val weeklyWidgetComponent = ComponentName(context, WeeklyViewWidget::class.java)
                            var appWidgetIds = widgetManager.getAppWidgetIds(weeklyWidgetComponent)
                            for (i in appWidgetIds.indices) {
                                WeeklyViewWidget.updateAppWidget(context, widgetManager, i)
                            }
                            val widgetComponent = ComponentName(context, DailyViewWidget::class.java)
                            appWidgetIds = widgetManager.getAppWidgetIds(widgetComponent)
                            for (i in appWidgetIds.indices) {
                                    val views = RemoteViews(
                                        context.packageName,
                                        R.layout.daily_view_widget
                                    )
                                    views.setRemoteAdapter(R.id.widgetListView, Intent(context, DailyViewWidgetRemoteViewsService::class.java))
                                    widgetManager.updateAppWidget(i, views)
                            }
                            val scheduleTiming = ScheduleTiming(schedule.scheduleSettings)
                            for (dayIndex in schedule.days.indices) {
                                val day = schedule.days[dayIndex]
                                val dayConstant = when (dayIndex) {
                                    0 -> DateTimeConstants.MONDAY
                                    1 -> DateTimeConstants.TUESDAY
                                    2 -> DateTimeConstants.WEDNESDAY
                                    3 -> DateTimeConstants.THURSDAY
                                    else -> DateTimeConstants.FRIDAY
                                }
                                for (periodIndex in day.periods.indices) {
                                    val period = day.periods[periodIndex]
                                    if (period.hasNotification || period.hasAlarm) {
                                        val delay: Long = scheduleTiming.getNotificationDelay(
                                            dayConstant, period,
                                            false
                                        )
                                        scheduleNotification(
                                            NotificationInfo(dayConstant, dayIndex, periodIndex), delay,
                                            context
                                        )
                                    }
                                }
                            }
                        } else {
                            Log.wtf(Constants.TAG, "Couldn't find users schedule")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e(Constants.TAG, "get failed with ", exception)
                    }
            }
        }
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
}