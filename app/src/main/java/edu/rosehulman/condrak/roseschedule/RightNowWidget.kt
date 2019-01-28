package edu.rosehulman.condrak.roseschedule

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.view.View
import android.widget.RemoteViews

class RightNowWidget : AppWidgetProvider() {

    val scheduleSettings = Constants.dummyScheduleSettings
    val schedule = Constants.dummySchedule
    val scheduleTiming = Constants.dummyScheduleTiming


    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, schedule, scheduleTiming)
        }
    }

    override fun onEnabled(context: Context) {
        scheduleTiming.init(context)
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {
        internal fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            schedule: Schedule,
            scheduleTiming: ScheduleTiming
        ) {
            val widgetText = context.getString(R.string.appwidget_text)
            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.fragment_right_now)
            views.setTextViewText(R.id.appwidget_text, widgetText)
            val currClass = scheduleTiming.getCurrentClass(schedule)
            val nextClass = scheduleTiming.getNextClass(schedule)
            if (currClass != null) {
                views.setTextViewText(R.id.currentClassPeriodNumber, currClass.getPeriodText())
                views.setTextViewText(R.id.currentClassInfo, if (currClass.hasLocation())
                    context.resources.getString(R.string.class_text, currClass.className, currClass.classLocation)
                else
                    currClass.className)
                views.setTextViewText(R.id.currentClassEndTime, context.resources.getString(R.string.class_end_time,
                    scheduleTiming.getEndTime(currClass).toString("h:mm a")))
                views.setTextViewText(R.id.currentClassEndTimeRelative, context.resources.getString(R.string.class_time_relative,
                    scheduleTiming.getRelativeEndTime(currClass)))
            } else {
                views.setViewVisibility(R.id.currentClassLayout, View.GONE)
            }
            if (nextClass != null) {
                views.setTextViewText(R.id.nextClassPeriodNumber, nextClass.getPeriodText())
                views.setTextViewText(R.id.nextClassInfo, if (nextClass.hasLocation())
                    context.resources.getString(R.string.class_text, nextClass.className, nextClass.classLocation)
                else
                    nextClass.className)
                views.setTextViewText(R.id.nextClassStartTime, context.resources.getString(R.string.class_start_time,
                    scheduleTiming.getStartTime(nextClass).toString("h:mm a")))
                views.setTextViewText(R.id.nextClassStartTimeRelative, context.resources.getString(R.string.class_time_relative,
                    scheduleTiming.getRelativeStartTime(nextClass)))
            } else {
                views.setViewVisibility(R.id.nextClassLayout, View.GONE)
            }


            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

