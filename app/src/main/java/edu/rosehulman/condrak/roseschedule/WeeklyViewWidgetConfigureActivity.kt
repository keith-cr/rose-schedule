package edu.rosehulman.condrak.roseschedule

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioGroup

/**
 * The configuration screen for the [WeeklyViewWidget] AppWidget.
 */
class WeeklyViewWidgetConfigureActivity : Activity() {
    internal var mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    internal lateinit var mRadioGroup: RadioGroup
    internal var mOnClickListener: View.OnClickListener = View.OnClickListener {
        val context = this@WeeklyViewWidgetConfigureActivity

        // When the button is clicked, store the string locally
        val widgetColor = if (mRadioGroup.checkedRadioButtonId == R.id.radioButtonWhite) R.color.white else R.color.textDarkPrimary
        saveColorPref(context, mAppWidgetId, widgetColor)

        // It is the responsibility of the configuration activity to update the app widget
        val appWidgetManager = AppWidgetManager.getInstance(context)
        WeeklyViewWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId)

        // Make sure we pass back the original appWidgetId
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(Activity.RESULT_CANCELED)

        setContentView(R.layout.weekly_view_widget_configure)
        mRadioGroup = findViewById<View>(R.id.radioGroup) as RadioGroup
        findViewById<View>(R.id.add_button).setOnClickListener(mOnClickListener)

        // Find the widget id from the intent.
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        mRadioGroup.check(if (loadColorPref(this@WeeklyViewWidgetConfigureActivity, mAppWidgetId) == R.color.white) R.id.radioButtonWhite else R.id.radioButtonBlack)
    }

    companion object {

        private val PREFS_NAME = "edu.rosehulman.condrak.roseschedule.WeeklyViewWidget"
        private val PREF_PREFIX_KEY = "appwidget_"

        // Write the prefix to the SharedPreferences object for this widget
        internal fun saveColorPref(context: Context, appWidgetId: Int, color: Int) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
            prefs.putInt(PREF_PREFIX_KEY + appWidgetId, color)
            prefs.apply()
        }

        // Read the prefix from the SharedPreferences object for this widget.
        // If there is no preference saved, get the default from a resource
        internal fun loadColorPref(context: Context, appWidgetId: Int): Int {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            val colorValue = prefs.getInt(PREF_PREFIX_KEY + appWidgetId, 0)
            return if (colorValue == 0) R.color.textDarkPrimary else colorValue
        }

        internal fun deleteColorPref(context: Context, appWidgetId: Int) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
            prefs.remove(PREF_PREFIX_KEY + appWidgetId)
            prefs.apply()
        }
    }
}

