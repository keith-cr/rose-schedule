package edu.rosehulman.condrak.roseschedule

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.google.gson.Gson

/**
 * Implementation of App Widget functionality.
 */
class WeeklyViewWidget : AppWidgetProvider() {

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete the preference associated with it.
        for (appWidgetId in appWidgetIds) {
            WeeklyViewWidgetConfigureActivity.deleteColorPref(context, appWidgetId)
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        val prefs = context.getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE)
        schedule = Gson().fromJson(prefs.getString(Constants.KEY_SCHEDULE, "")!!, Schedule::class.java)
        scheduleSettings = schedule!!.scheduleSettings
        scheduleTiming = ScheduleTiming(scheduleSettings!!)
        scheduleTiming?.init(context)
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        val prefs = context.getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE)
        schedule = Gson().fromJson(prefs.getString(Constants.KEY_SCHEDULE, "")!!, Schedule::class.java)
        scheduleSettings = schedule!!.scheduleSettings
        scheduleTiming = ScheduleTiming(scheduleSettings!!)
        scheduleTiming?.init(context)
    }

    companion object {
        var scheduleSettings: ScheduleSettings? = null
        var schedule: Schedule? = null
        var scheduleTiming: ScheduleTiming? = null

        internal fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val widgetColor = WeeklyViewWidgetConfigureActivity.loadColorPref(context, appWidgetId)

            val views = RemoteViews(context.packageName, R.layout.weekly_view_widget)
            if (schedule != null && scheduleTiming != null) {
                for (day in 0 until 5) {
                    val dayText = when (day) {
                        0 -> R.id.monday
                        1 -> R.id.tuesday
                        2 -> R.id.wednesday
                        3 -> R.id.thursday
                        else -> R.id.friday
                    }
                    views.setTextColor(dayText, context.resources.getColor(widgetColor, null))
                    for (period in 0 until 10) {
                        if (day == 0) {
                            val classPeriod = schedule!!.days[day].periods[period]
                            val id = context.resources.getIdentifier(
                                "_${period + 1}_period_title",
                                "id",
                                context.packageName
                            )
                            views.setTextViewText(
                                id, context.resources.getString(
                                    R.string.period_title,
                                    classPeriod.shortPeriodText(),
                                    scheduleTiming!!.getStartTime(classPeriod).toString("h:mm")
                                )
                            )
                            views.setTextColor(id, context.resources.getColor(widgetColor, null))
                        }
                        val id = context.resources.getIdentifier("day${day}period$period", "id", context.packageName)
                        val classPeriod = schedule!!.days[day].periods[period]
                        if (classPeriod.hasLocation())
                            views.setTextViewText(
                                id, context.resources.getString(
                                    R.string.class_text,
                                    classPeriod.className, classPeriod.classLocation
                                )
                            )
                        else
                            views.setTextViewText(id, classPeriod.className)
                        views.setTextColor(id, context.resources.getColor(widgetColor, null))
                    }
                }

                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }
}

