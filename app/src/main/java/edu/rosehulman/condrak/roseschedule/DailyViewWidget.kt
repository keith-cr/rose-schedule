package edu.rosehulman.condrak.roseschedule

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews


/**
 * Implementation of App Widget functionality.
 */
class DailyViewWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(
                context.packageName,
                R.layout.daily_view_widget
            )
            val intent = Intent(context, DailyViewWidgetRemoteViewsService::class.java)
            views.setRemoteAdapter(R.id.widgetListView, intent)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

