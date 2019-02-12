package edu.rosehulman.condrak.roseschedule

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.TimePicker
import java.util.*

class TimePickerFragment: DialogFragment(), TimePickerDialog.OnTimeSetListener {

    private var listener: TimeSetListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        var hour = c.get(Calendar.HOUR_OF_DAY)
        var minute = c.get(Calendar.MINUTE)

        arguments?.let {
            hour = it.getInt(ARG_HOUR, hour)
            minute = it.getInt(ARG_MINUTE, minute)
        }

        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(activity, this, hour, minute, false)
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        listener?.onTimeSet(view, hourOfDay, minute)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is TimeSetListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
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
    interface TimeSetListener {
        fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int)
    }

    companion object {
        const val ARG_HOUR = "hour"
        const val ARG_MINUTE = "minute"

        @JvmStatic
        fun newInstance(hour: Int, minute: Int): TimePickerFragment =
            TimePickerFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_HOUR, hour)
                    putInt(ARG_MINUTE, minute)
                }
            }
    }
}
