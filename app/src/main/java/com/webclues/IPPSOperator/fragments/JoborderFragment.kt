package com.webclues.IPPSOperator.fragments


import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.tabs.TabLayout
import com.webclues.IPPSOperator.R
import com.webclues.IPPSOperator.activity.MainActivity
import com.webclues.IPPSOperator.fragments.Joborderlist.*
import com.webclues.IPPSOperator.utility.Content
import com.webclues.IPPSOperator.utility.CustomViewPager
import com.webclues.IPPSOperator.utility.Log


/**
 * A simple [Fragment] subclass.
 */
class JoborderFragment : Fragment() {

    lateinit var tabLayout: TabLayout
    lateinit var viewpager: CustomViewPager
    lateinit var tvpriority: TextView
    lateinit var lnPriority: LinearLayout
    private var prioritytypes: ArrayList<String>? = arrayListOf()
    lateinit var priorityDropdownView: ListPopupWindow
    lateinit var frameLayout: FrameLayout
    lateinit var priority: String
    lateinit var fragment: Fragment
    lateinit var manager: FragmentManager
    lateinit var fragmentTransaction: FragmentTransaction


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        val view: View = inflater.inflate(R.layout.fragment_joborder, container, false)
        Log.e("Job Order Frag-->", "onCreateView")
        initview(view)
        return view
    }

    private fun initview(view: View) {
        Content.FILTER_PRIORITY = Content.ALL
        viewpager = view.findViewById(R.id.viewpager)
        frameLayout = view.findViewById(R.id.frame_container)
        tvpriority = view.findViewById(R.id.tvpriority)
        lnPriority = view.findViewById(R.id.lnPriority)
        viewpager.setPagingEnabled(false)
        tabLayout = view.findViewById(R.id.tablayout)


        if (arguments != null) {

            var value = getArguments()!!.getString("JobType")
            if (value.equals(Content.JOBREQUEST_STATUS)) {
                tabLayout.getTabAt(0)!!.select()
                changefragment(JobRequestFragment(), false)
            } else if (value.equals(Content.WORKORDER_STARUS)) {
                tabLayout.getTabAt(1)!!.select()
                changefragment(WorkOrderFragment(), false)
            } else {
                tabLayout.getTabAt(2)!!.select()
                changefragment(CompletedFragment(), false)
            }

        } else {
            fragment = JobRequestFragment()
            manager = this.childFragmentManager
            fragmentTransaction = manager.beginTransaction()
            fragmentTransaction.replace(R.id.frame_container, fragment)
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            fragmentTransaction.commit()

        }


        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab!!.position) {
                    0 -> {
                        fragment = JobRequestFragment()
                    }
                    1 -> {
                        fragment = WorkOrderFragment()
                    }
                    2 -> {
                        fragment = CompletedFragment()
                    }

                }
                val fm: FragmentManager = childFragmentManager
                val ft = fm.beginTransaction()
                ft.replace(R.id.frame_container, fragment)
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                ft.commit()

            }

        })

        val typeface = Typeface.createFromAsset(context!!.assets, "font/lato_semibold.ttf")
        tabLayout.applyFont(typeface)

        Setpriority()
    }

    private fun changefragment(fragment: Fragment, Backenable: Boolean) {

        manager = this.childFragmentManager
        fragmentTransaction = manager.beginTransaction()
        fragmentTransaction.add(R.id.frame_container, fragment)
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        if (Backenable) {
            fragmentTransaction.addToBackStack(null)
        }
        fragmentTransaction.commit()
    }

    private fun Setpriority() {
        prioritytypes =
            resources.getStringArray(R.array.priority_item).toCollection(ArrayList())
        priorityDropdownView = ListPopupWindow(activity!!)
        priorityDropdownView.setAdapter(
            ArrayAdapter(
                activity!!,
                android.R.layout.simple_spinner_dropdown_item,
                prioritytypes!!
            )
        )
        priorityDropdownView.anchorView = lnPriority
        priorityDropdownView.isModal = true
        lnPriority.post {
            priorityDropdownView.setDropDownGravity(Gravity.END)
            priorityDropdownView.width = 350
            priorityDropdownView.horizontalOffset = -15
        }

        priorityDropdownView.setOnItemClickListener { adapterView, view, position, viewId ->
            tvpriority.text = prioritytypes!!.get(position)
            priorityDropdownView.dismiss()
            val intent = Intent("SelectedPriority")
            if (prioritytypes!!.get(position).equals(getString(R.string.pending))) {
                Content.STATUS_PRIORITY = getString(R.string.pending)

            } else if (prioritytypes!!.get(position).equals(getString(R.string.low))) {
                Content.STATUS_PRIORITY = getString(R.string.low)
            } else if (prioritytypes!!.get(position).equals(getString(R.string.medium))) {
                Content.STATUS_PRIORITY = getString(R.string.medium)

            } else if (prioritytypes!!.get(position).equals(getString(R.string.high))) {
                Content.STATUS_PRIORITY = getString(R.string.high)

            } else {
                Content.STATUS_PRIORITY = getString(R.string.all)

            }
            intent.putExtra(Content.PRIORITY, Content.STATUS_PRIORITY)

            LocalBroadcastManager.getInstance(activity!!).sendBroadcast(intent)
        }
        tvpriority.setOnClickListener {
            priorityDropdownView.show()
        }
    }



    override fun onResume() {
        super.onResume()
        (activity as MainActivity).txtTitle.text = resources.getString(R.string.maintenance_app)
    }


    fun TabLayout.applyFont(typeface: Typeface) {
        val viewGroup = getChildAt(0) as ViewGroup
        val tabsCount = viewGroup.childCount
        for (j in 0 until tabsCount) {
            val viewGroupChildAt = viewGroup.getChildAt(j) as ViewGroup
            val tabChildCount = viewGroupChildAt.childCount
            for (i in 0 until tabChildCount) {
                val tabViewChild = viewGroupChildAt.getChildAt(i)
                if (tabViewChild is TextView) {
                    tabViewChild.typeface = typeface
                }
            }
        }
    }


}


