package edu.rosehulman.condrak.roseschedule

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.google.gson.Gson




class DailyViewWidgetRemoteViewsFactory(val context: Context, val intent: Intent): RemoteViewsService.RemoteViewsFactory {

    private var scheduleSettings: ScheduleSettings? = null
    private var schedule: Schedule? = null
    private var scheduleTiming: ScheduleTiming? = null

    override fun onCreate() {
        val prefs = context.getSharedPreferences(Constants.PREFS, MODE_PRIVATE)
        schedule = Gson().fromJson(prefs.getString(Constants.KEY_SCHEDULE, "")!!, Schedule::class.java)
        scheduleSettings = schedule!!.scheduleSettings
        scheduleTiming = ScheduleTiming(scheduleSettings!!)
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return schedule!!.days[scheduleTiming!!.getCurrentClassDay()].periods[position].hashCode().toLong()
    }

    override fun onDataSetChanged() {
        //noop
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getViewAt(position: Int): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.daily_view_widget_list_item)
        val period = schedule!!.days[scheduleTiming!!.getCurrentClassDay()].periods[position]
        views.setTextViewText(R.id.periodTextView, period.getPeriodText())
        if (period.hasLocation())
            views.setTextViewText(R.id.classTextView, context.resources.getString(R.string.class_text, period.className, period.classLocation))
        else
            views.setTextViewText(R.id.classTextView, period.className)
        views.setTextViewText(R.id.startTimeTextView, scheduleTiming!!.getStartTime(period).toString("h:mm aa"))
        views.setTextViewText(R.id.endTimeTextView, scheduleTiming!!.getEndTime(period).toString("h:mm aa"))
        return views
    }

    override fun getCount(): Int {
        return schedule!!.days[scheduleTiming!!.getCurrentClassDay()].periods.size
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun onDestroy() {
        //noop
    }
}