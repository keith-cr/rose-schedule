package edu.rosehulman.condrak.roseschedule

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews

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
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        scheduleTiming.init(context)
    }

    companion object {
        val scheduleSettings = Constants.dummyScheduleSettings
        val schedule = Constants.dummySchedule
        val scheduleTiming = Constants.dummyScheduleTiming

        internal fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val widgetColor = WeeklyViewWidgetConfigureActivity.loadColorPref(context, appWidgetId)

            val views = RemoteViews(context.packageName, R.layout.weekly_view_widget)
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
                        val classPeriod = schedule.days[day].periods[period]
                        val id = context.resources.getIdentifier("_${period + 1}_period_title", "id", context.packageName)
                        views.setTextViewText(id, context.resources.getString(R.string.period_title,
                            classPeriod.getShortPeriodText(), scheduleTiming.getStartTime(classPeriod).toString("h:mm")))
                        views.setTextColor(id, context.resources.getColor(widgetColor, null))
                    }
                    val id = context.resources.getIdentifier("day${day}period$period", "id", context.packageName)
                    val classPeriod = schedule.days[day].periods[period]
                    if (classPeriod.hasLocation())
                         views.setTextViewText(id, context.resources.getString(R.string.class_text,
                            classPeriod.className, classPeriod.classLocation))
                    else
                        views.setTextViewText(id, classPeriod.className)
                    views.setTextColor(id, context.resources.getColor(widgetColor, null))
                }
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

