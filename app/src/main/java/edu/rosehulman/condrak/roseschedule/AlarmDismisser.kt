package edu.rosehulman.condrak.roseschedule

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.Ringtone

class AlarmDismisser: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val bundle = intent.getBundleExtra(Constants.KEY_BUNDLE) ?: return
        val notificationId = bundle.getInt(Constants.KEY_NOTIFICATION_ID)
        ringtone!!.stop()
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        notificationManager.cancel(notificationId)
    }

    companion object {
        var ringtone: Ringtone? = null
    }
}