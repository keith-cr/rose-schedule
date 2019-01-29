package edu.rosehulman.condrak.roseschedule

import android.content.Intent
import android.widget.RemoteViewsService

class DailyViewWidgetRemoteViewsService: RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return DailyViewWidgetRemoteViewsFactory(this.applicationContext, intent)
    }
}