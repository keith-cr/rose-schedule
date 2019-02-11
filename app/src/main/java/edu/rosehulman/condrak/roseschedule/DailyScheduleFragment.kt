package edu.rosehulman.condrak.roseschedule

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [DailyScheduleFragment.OnListFragmentInteractionListener] interface.
 */
class DailyScheduleFragment : Fragment() {

    private var schedule: Schedule? = null
    private var scheduleTiming: ScheduleTiming? = null

    private var listener: OnListFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            schedule = it.getParcelable(ARG_SCHEDULE)
            scheduleTiming = it.getParcelable(ARG_SCHEDULE_TIMING)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dayName:String = schedule!!.days[scheduleTiming!!.getCurrentClassDay()].name
        activity?.title = resources.getString(R.string.daily_fragment_title, dayName)
        val view = inflater.inflate(R.layout.fragment_daily_schedule_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)
                adapter = DailyScheduleRecyclerViewAdapter(schedule!!, scheduleTiming!!, listener)
            }
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnListFragmentInteractionListener")
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
    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(item: Schedule?)
    }

    companion object {
        const val ARG_SCHEDULE = "schedule"
        const val ARG_SCHEDULE_TIMING = "scheduleTiming"

        @JvmStatic
        fun newInstance(schedule: Schedule, scheduleTiming: ScheduleTiming) =
            DailyScheduleFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_SCHEDULE, schedule)
                    putParcelable(ARG_SCHEDULE_TIMING, scheduleTiming)
                }
            }
    }
}
