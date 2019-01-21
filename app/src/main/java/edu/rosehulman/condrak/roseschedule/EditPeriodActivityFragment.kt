package edu.rosehulman.condrak.roseschedule

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
        activity?.title = schedule!!.days[day!!].periods[period!!].getPeriodText()
        return view
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
