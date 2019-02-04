package edu.rosehulman.condrak.roseschedule

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_edit_period.view.*

/**
 * A placeholder fragment containing a simple view.
 */
class EditPeriodActivityFragment : Fragment() {

    private var schedule: Schedule? = null
    private var scheduleTiming: ScheduleTiming? = null
    private var day: Int? = null
    private var period: Int? = null

    private var listener: OnSaveListener? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let {
            schedule = it.getParcelable(ARG_SCHEDULE)
            scheduleTiming = it.getParcelable(ARG_SCHEDULE_TIMING)
            day = it.getInt(ARG_DAY)
            period = it.getInt(ARG_PERIOD)
        }
        val view = inflater.inflate(R.layout.fragment_edit_period, container, false)
        if (schedule!!.days[day!!].periods[period!!].isFree) {
            view.switchFreePeriod.isChecked = true
            view.name_text_input_layout.visibility = View.GONE
            view.location_text_input_layout.visibility = View.GONE
        } else {
            view.editClass.setText(schedule!!.days[day!!].periods[period!!].className)
            view.editLocation.setText(schedule!!.days[day!!].periods[period!!].classLocation)
        }
        view.switchFreePeriod.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                view.name_text_input_layout.visibility = View.GONE
                view.location_text_input_layout.visibility = View.GONE
            } else {
                view.editClass.setText(schedule!!.days[day!!].periods[period!!].className)
                view.editLocation.setText(schedule!!.days[day!!].periods[period!!].classLocation)
                view.name_text_input_layout.visibility = View.VISIBLE
                view.location_text_input_layout.visibility = View.VISIBLE
            }
        }
        view.saveButton.setOnClickListener {
            val classPeriod: ClassPeriod = if (view.switchFreePeriod.isChecked) {
                ClassPeriod(period!!+1)
            } else {
                ClassPeriod(period!!+1, view.editClass.text.toString(), view.editLocation.text.toString())
            }
            listener?.onSave(classPeriod)
        }
        activity?.title = schedule!!.days[day!!].periods[period!!].getPeriodText()
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnSaveListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnSaveListener")
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
    interface OnSaveListener {
        fun onSave(classPeriod: ClassPeriod)
    }

    companion object {
        const val ARG_SCHEDULE = "schedule"
        const val ARG_DAY = "day"
        const val ARG_PERIOD = "period"
        const val ARG_SCHEDULE_TIMING = "scheduleTiming"

        @JvmStatic
        fun newInstance(schedule: Schedule, day: Int, period: Int, scheduleTiming: ScheduleTiming) =
            EditPeriodActivityFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_SCHEDULE, schedule)
                    putInt(ARG_DAY, day)
                    putInt(ARG_PERIOD, period)
                    putParcelable(ARG_SCHEDULE_TIMING, scheduleTiming)
                }
            }
    }
}
