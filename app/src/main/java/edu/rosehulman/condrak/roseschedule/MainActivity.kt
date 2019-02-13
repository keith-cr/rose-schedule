package edu.rosehulman.condrak.roseschedule

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.RemoteViews
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import org.joda.time.DateTimeConstants

// Fragment switching code from https://stackoverflow.com/a/46600951
class MainActivity : AppCompatActivity(), DailyScheduleFragment.OnListFragmentInteractionListener {

    enum class CurrentFragment {
        RIGHT_NOW, DAILY_VIEW, WEEKLY_VIEW
    }

    private lateinit var schedule: Schedule
    private lateinit var scheduleTiming: ScheduleTiming
    private lateinit var currentFragment: CurrentFragment

    private var uid = ""
    private lateinit var scheduleRef: DocumentReference
    private lateinit var listenerRegistration: ListenerRegistration

    private fun addSnapshotListener() {
        listenerRegistration = scheduleRef
            .addSnapshotListener { documentSnapshot, e ->
                if (e != null) {
                    Log.w(Constants.TAG, "listen error", e)
                } else {
                    processSnapshotChanges(documentSnapshot!!)
                }
            }
    }

    private fun removeRegisteredNotifications() {
        val prefs = getSharedPreferences(Constants.PREFS, MODE_PRIVATE)
        val requestCodesWrapperString = prefs.getString(Constants.KEY_REQUEST_CODES, null)
        val requestCodesWrapper = if (requestCodesWrapperString != null)
            Gson().fromJson(requestCodesWrapperString, RequestCodesWrapper::class.java)
        else
            RequestCodesWrapper(ArrayList())
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        for (requestCode in requestCodesWrapper.requestCodes) {
            val notificationIntent = Intent(applicationContext, NotificationPublisher::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                applicationContext, requestCode,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
            )
            alarmManager.cancel(pendingIntent)
        }
        requestCodesWrapper.requestCodes.clear()
        val editor = prefs.edit()
        editor.putString(Constants.KEY_REQUEST_CODES, Gson().toJson(requestCodesWrapper))
        editor.apply()
    }

    private fun registerNotifications() {
        removeRegisteredNotifications()
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
                    val delay: Long = scheduleTiming.getNotificationDelay(dayConstant, period, false)
                    scheduleNotification(NotificationInfo(dayConstant, dayIndex, periodIndex), delay)
                }
            }
        }
    }

    private fun scheduleNotification(notificationInfo: NotificationInfo, delay: Long) {
        val bundle = Bundle()
        bundle.putParcelable(NotificationPublisher.NOTIFICATION_INFO, notificationInfo)
        val notificationIntent = Intent(applicationContext, NotificationPublisher::class.java)
        notificationIntent.putExtra(Constants.KEY_BUNDLE, bundle)
        val requestCode = System.currentTimeMillis().toInt()
        val pendingIntent = PendingIntent.getBroadcast(applicationContext, requestCode,
            notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val futureInMillis = SystemClock.elapsedRealtime() + delay
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent)

        val prefs = getSharedPreferences(Constants.PREFS, MODE_PRIVATE)
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

    private fun processSnapshotChanges(documentSnapshot: DocumentSnapshot) {
        schedule = documentSnapshot.toObject(Schedule::class.java)!!
        scheduleTiming = ScheduleTiming(schedule.scheduleSettings)
        scheduleTiming.init(this)
        val prefs = getSharedPreferences(Constants.PREFS, MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(Constants.KEY_SCHEDULE, Gson().toJson(schedule))
        editor.apply()
        updateWidgets()
        registerNotifications()
        val ft = supportFragmentManager.beginTransaction()
        val fragment: Fragment = when (currentFragment) {
            CurrentFragment.RIGHT_NOW -> RightNowFragment.newInstance(schedule, scheduleTiming)
            CurrentFragment.DAILY_VIEW -> DailyScheduleFragment.newInstance(schedule, scheduleTiming)
            CurrentFragment.WEEKLY_VIEW -> WeeklyViewFragment.newInstance(schedule, scheduleTiming)
        }
        ft.replace(R.id.content, fragment)
        ft.commitAllowingStateLoss()
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    private fun updateWidgets() {
        val widgetManager = AppWidgetManager.getInstance(this)
        val weeklyWidgetComponent = ComponentName(this, WeeklyViewWidget::class.java)
        var appWidgetIds = widgetManager.getAppWidgetIds(weeklyWidgetComponent)
        for (i in appWidgetIds.indices) {
            WeeklyViewWidget.updateAppWidget(this, widgetManager, i)
        }
        val widgetComponent = ComponentName(this, DailyViewWidget::class.java)
        appWidgetIds = widgetManager.getAppWidgetIds(widgetComponent)
        for (i in appWidgetIds.indices) {
            val views = RemoteViews(
                this.packageName,
                R.layout.daily_view_widget
            )
            views.setRemoteAdapter(R.id.widgetListView, Intent(this, DailyViewWidgetRemoteViewsService::class.java))
            widgetManager.updateAppWidget(i, views)
        }

    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_right_now -> {
                supportFragmentManager.beginTransaction().replace(R.id.content, RightNowFragment.newInstance(schedule, scheduleTiming)).commit()
                currentFragment = CurrentFragment.RIGHT_NOW
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_daily_view -> {
                supportFragmentManager.beginTransaction().replace(R.id.content, DailyScheduleFragment.newInstance(schedule, scheduleTiming)).commit()
                currentFragment = CurrentFragment.DAILY_VIEW
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_weekly_view -> {
                supportFragmentManager.beginTransaction().replace(R.id.content, WeeklyViewFragment.newInstance(schedule, scheduleTiming)).commit()
                currentFragment = CurrentFragment.WEEKLY_VIEW
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            uid = savedInstanceState.getString(UID)!!
            currentFragment = CurrentFragment.valueOf(savedInstanceState.getString(CURRENT_FRAGMENT)!!)
        } else {
            currentFragment = CurrentFragment.RIGHT_NOW
            uid = intent.getStringExtra(UID)
        }
        setContentView(R.layout.activity_main)
        scheduleRef = FirebaseFirestore
            .getInstance()
            .collection(Constants.USERS_COLLECTION)
            .document(uid)
        addSnapshotListener()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        when (id) {
            R.id.action_edit_schedule -> {
                val intent = Intent(this, EditDayActivity::class.java).apply {
                    putExtra(UID, uid)
                }
                startActivity(intent)
            }
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java).apply {
                    putExtra(UID, uid)
                }
                startActivity(intent)
            }
            R.id.action_logout -> {
                val auth = FirebaseAuth.getInstance()
                auth.signOut()
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onListFragmentInteraction(item: Schedule?) {

    }

    override fun onSaveInstanceState(outState: Bundle?) {
        // Save the user's current game state
        outState?.run {
            putString(CURRENT_FRAGMENT, currentFragment.name)
            putString(UID, uid)
        }

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState)
    }

    companion object {
        const val CURRENT_FRAGMENT = "currentFragment"
        const val UID = "uid"
    }
}