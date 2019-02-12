package edu.rosehulman.condrak.roseschedule

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import kotlinx.android.synthetic.main.fragment_settings.view.*

/**
 * A placeholder fragment containing a simple view.
 */
class SettingsActivityFragment : Fragment() {

    private var schedule: Schedule? = null

    private var listener: OnFragmentInteractionListener? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let {
            schedule = it.getParcelable(ARG_SCHEDULE)
        }
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        view.editPeriodLength.setText(schedule!!.scheduleSettings.periodLength.toString())
        view.editPassingPeriodLength.setText(schedule!!.scheduleSettings.passingPeriodLength.toString())
        view.editFirstPeriodStartTime.text = schedule!!.scheduleSettings.firstPeriodStartTime
        view.editFirstPeriodStartTime.setOnClickListener {
            val parts = view.editFirstPeriodStartTime.text.toString().split(":")
            var hour = parts[0].toInt()
            val minute = parts[1].substring(0, 2).toInt()
            val amPM = parts[1].substring(3)
            if (amPM == "PM") {
                if (hour != 12)
                    hour += 12
            }
            else if (hour == 12)
                hour = 0
            listener?.onTimePickerButtonClicked(hour, minute)
        }
        view.saveButton.setOnClickListener {
            schedule!!.scheduleSettings.periodLength = view.editPeriodLength.text.toString().toInt()
            schedule!!.scheduleSettings.passingPeriodLength = view.editPassingPeriodLength.text.toString().toInt()
            schedule!!.scheduleSettings.firstPeriodStartTime = view.editFirstPeriodStartTime.text.toString()
            listener?.onSave(schedule!!)
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        val firstPeriodStartTime = if (hourOfDay > 12) {
            if (minute < 10)
                "${hourOfDay-12}:0$minute PM"
            else
                "${hourOfDay-12}:$minute PM"
        } else if (hourOfDay == 0) {
            if (minute < 10)
                "12:0$minute AM"
            else
                "12:$minute AM"
        } else if (hourOfDay == 12) {
            if (minute < 10)
                "$hourOfDay:0$minute PM"
            else
                "$hourOfDay:$minute PM"
        } else {
            if (minute < 10)
                "$hourOfDay:0$minute AM"
            else
                "$hourOfDay:$minute AM"
        }
        getView()?.editFirstPeriodStartTime?.text = firstPeriodStartTime
        Log.i(Constants.TAG, "Set First Period Start Time: $firstPeriodStartTime")
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        fun onSave(schedule: Schedule)
        fun onTimePickerButtonClicked(hour: Int, minute: Int)
    }

    companion object {
        private const val ARG_SCHEDULE = "schedule"

        @JvmStatic
        fun newInstance(schedule: Schedule) =
            SettingsActivityFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_SCHEDULE, schedule)
                }
            }
    }
}
