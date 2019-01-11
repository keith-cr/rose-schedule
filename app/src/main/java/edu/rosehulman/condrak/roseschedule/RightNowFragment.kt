package edu.rosehulman.condrak.roseschedule


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

private const val ARG_SCHEDULE = "schedule"

/**
 * A simple [Fragment] subclass.
 * Use the [RightNowFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class RightNowFragment : Fragment() {
    private var schedule: Schedule? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            schedule = it.getParcelable(ARG_SCHEDULE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_right_now, container, false)
    }


    companion object {
        @JvmStatic
        fun newInstance(schedule: Schedule) =
            RightNowFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_SCHEDULE, schedule)
                }
            }
    }
}
