package com.webclues.IPPSOperator.fragments


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.webclues.IPPSOperator.R
import com.webclues.IPPSOperator.activity.AddJobRequestActivity
import com.webclues.IPPSOperator.activity.MainActivity
import com.webclues.IPPSOperator.utility.Content
import com.webclues.IPPSOperator.utility.Log


/**
 * A simple [Fragment] subclass.
 */
class NewHomeFragment : Fragment(), BottomNavigationView.OnNavigationItemSelectedListener {


    lateinit var bottomnavigationview: BottomNavigationView
    var selectedMenu: Int = 0
    lateinit var fragmentTransaction: FragmentTransaction
    lateinit var fragmentmanager: FragmentManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_new_home, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initview(view)
    }

    private fun initview(view: View) {
        fragmentmanager = activity!!.supportFragmentManager
        bottomnavigationview = view.findViewById(R.id.bottomnavigation)


        bottomnavigationview.setOnNavigationItemSelectedListener(this)
        Log.e("selectedMenu-->", selectedMenu)
        if (selectedMenu == 0) {
            bottomnavigationview.selectedItemId = R.id.jobordermenu
        } else {

            bottomnavigationview.selectedItemId = selectedMenu
            //            bottomnavigationview?.get(selectedMenu)?.performClick()

        }


        showjobstatusbadge(context, bottomnavigationview, R.id.jobordermenu)
        showNotificationsBadge(context, bottomnavigationview, R.id.notificationmenu)

    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.jobordermenu -> {
                if (getCurrentFragment() !is JoborderFragment) {
                    selectedMenu = R.id.jobordermenu
                    changefragment(JoborderFragment(), false)
                }


            }
            R.id.jobrequestmenu -> {

                startActivityForResult(
                    Intent(activity, AddJobRequestActivity::class.java),
                    Content.REQ_JOB
                )

            }
            R.id.notificationmenu -> {
                if (getCurrentFragment() !is NewNotificationsFragment) {
                    selectedMenu = R.id.notificationmenu
                    changefragment(NewNotificationsFragment(), false)
                }


            }
            R.id.chatmenu -> {
                if (getCurrentFragment() !is ChatFragment) {
                    selectedMenu = R.id.chatmenu

                    changefragment(ChatFragment(), false)
                }
            }
        }
        return true
    }


    fun changefragment(fragment: Fragment, backenable: Boolean) {
        fragmentTransaction = fragmentmanager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        if (backenable) {
            fragmentTransaction.addToBackStack(null)
        }
        fragmentTransaction.commit()

    }


    private fun getCurrentFragment(): Fragment? {
        return childFragmentManager.findFragmentById(R.id.fragment_container)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Content.REQ_JOB) {
            if (resultCode == Activity.RESULT_CANCELED) {
                bottomnavigationview.selectedItemId = selectedMenu
            }

        } else {
            if (requestCode == 100) {
                if (resultCode == Activity.RESULT_OK) {
                    if (Content.JOBTYPE.equals(Content.NOTIFICATION)) {
                        changefragment(NewNotificationsFragment(), false)
                    } else {
                        val fragment = JoborderFragment()
                        val args = Bundle()
                        args.putString("JobType", Content.JOBTYPE)
                        fragment.setArguments(args)
                        fragmentManager!!.beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .commit()

                    }

                }
            }
        }
    }


    fun showjobstatusbadge(
        context: Context?, bottomNavigationView: BottomNavigationView,
        itemId: Int
    ) {

        val itemView: BottomNavigationItemView = bottomNavigationView.findViewById(itemId)
        val badge: View = LayoutInflater.from(context)
            .inflate(R.layout.jobstatus_badge, bottomNavigationView, false)
        (activity as MainActivity).BadgeJobStatus = badge.findViewById(R.id.BadgeJobStatus)

        itemView.addView(badge)

    }

    fun showNotificationsBadge(
        context: Context?,
        bottomNavigationView: BottomNavigationView,
        itemid: Int
    ) {
        val itemView1: BottomNavigationItemView = bottomNavigationView.findViewById(itemid)
        val badge1: View = LayoutInflater.from(context)
            .inflate(R.layout.notification_badge, bottomNavigationView, false)
        (activity as MainActivity).BadgeNotification = badge1.findViewById(R.id.BadgeNotification)
        itemView1.addView(badge1)
    }


}
