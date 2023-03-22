package com.webclues.IPPSOperator.fragments.Joborderlist


import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.webclues.IPPSOperator.service.APIClient
import com.webclues.IPPSOperator.Modelclass.JobStatusModel

import com.webclues.IPPSOperator.R
import com.webclues.IPPSOperator.activity.MainActivity
import com.webclues.IPPSOperator.adapter.JobOrderAdapter
import com.webclues.IPPSOperator.adapter.PaginationScrollListener
import com.webclues.IPPSOperator.service.checkNetworkState
import com.webclues.IPPSOperator.utility.Content
import com.webclues.IPPSOperator.utility.CustomProgress
import com.webclues.IPPSOperator.utility.Log
import com.webclues.IPPSOperator.utility.Utility
import com.webclues.IPPSOperator.service.ApiInterface
import kotlinx.android.synthetic.main.fragment_all.*
import kotlinx.android.synthetic.main.fragment_faq.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 */
class WorkOrderFragment : Fragment() {

    lateinit var srlJobs: SwipeRefreshLayout
    lateinit var rvJobs: RecyclerView
    lateinit var customProgress: CustomProgress
    lateinit var apiInterface: ApiInterface
    var jobOrderAdapter: JobOrderAdapter? = null
    var jobstatuslist: ArrayList<JobStatusModel>? = arrayListOf()
    var PAGE_START: Int = 1
    var currentpage: Int = PAGE_START
    var isloading: Boolean = false
    var islastpage: Boolean = false


    private val mReceiver =
        object : BroadcastReceiver() {

            override fun onReceive(context: Context, intent: Intent) {
                Content.STATUS_PRIORITY = intent.getStringExtra(Content.PRIORITY)
                if (Content.STATUS_PRIORITY.equals(getString(R.string.pending))) {
                    Content.FILTER_PRIORITY = Content.PENDING
                } else if (Content.STATUS_PRIORITY.equals(getString(R.string.low))) {
                    Content.FILTER_PRIORITY = Content.LOW
                } else if (Content.STATUS_PRIORITY.equals(getString(R.string.medium))) {
                    Content.FILTER_PRIORITY = Content.MEDIUM
                } else if (Content.STATUS_PRIORITY.equals(getString(R.string.high))) {
                    Content.FILTER_PRIORITY = Content.HIGH
                } else {
                    Content.FILTER_PRIORITY = Content.ALL
                }
                islastpage = false
                jobOrderAdapter!!.jobstatuslist!!.clear()
                jobOrderAdapter!!.isloadingadded = false
                loadFirstpage()
            }


        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocalBroadcastManager.getInstance(activity!!)
            .registerReceiver(mReceiver, IntentFilter("SelectedPriority"))

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_all, container, false)
        initView(view)
        return view
    }


    fun initView(view: View) {
        apiInterface = APIClient.getretrofit(context!!).create(ApiInterface::class.java)
        customProgress = CustomProgress.instance
        isloading = false
        islastpage = false

        Content.JOBTYPE = Content.WORKORDER_STARUS

        srlJobs = view.findViewById(R.id.srlJobs)
        rvJobs = view.findViewById(R.id.rvJobs)
        rvJobs.layoutManager = WrapContentLinearLayoutManager(
            activity!!
        )
        jobOrderAdapter = JobOrderAdapter(activity!!)
        rvJobs.adapter = jobOrderAdapter
        rvJobs.addOnScrollListener(object :
            PaginationScrollListener(rvJobs.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                isloading = true
                currentpage += 1
                loadNextPage(currentpage, Content.ASSIGN_WORKORDER, Content.FILTER_PRIORITY, "")
            }

            override fun isLoading(): Boolean {

                return isloading
            }

            override fun isLastPage(): Boolean {

                return islastpage
            }


        })

        if (checkNetworkState(context!!)) {
            loadFirstpage()
        } else {
            Utility().showOkDialog(
                context!!,
                resources.getString(R.string.app_name),
                resources.getString(R.string.error_internet_ln)
            )
        }

        srlJobs.setOnRefreshListener {
            islastpage = false
            jobOrderAdapter!!.jobstatuslist!!.clear()
            loadFirstpage()
        }

    }


    fun loadFirstpage(

    ) {
        srlJobs.isRefreshing = false
        currentpage = PAGE_START
        customProgress.showProgress(context!!, getString(R.string.please_wait), false)
        val call: Call<JsonObject> =
            apiInterface.jobstatus(
                currentpage,
                Content.ASSIGN_WORKORDER,
                Content.FILTER_PRIORITY,
                ""
            )
        call.enqueue(object : Callback<JsonObject> {

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                customProgress.hideProgress()
                val jsonObject = JSONObject(response.body().toString())
                val statuscode = jsonObject.optInt("status_code")
                if (statuscode == 200) {
                    if (jsonObject.optBoolean("status")) {
                        val data = jsonObject.optJSONObject("data")
                        val joblist = data.optJSONArray("job_list")
                        if (data.optInt("unread_notification_count") != 0) {
                            (activity as MainActivity).BadgeNotification!!.visibility = View.VISIBLE
                        } else {
                            (activity as MainActivity).BadgeNotification!!.visibility = View.GONE

                        }
                        if (data.optInt("unread_job_request_count") != 0) {
                            (activity as MainActivity).BadgeJobStatus!!.visibility = View.VISIBLE
                        } else {
                            (activity as MainActivity).BadgeJobStatus!!.visibility = View.GONE

                        }
                        if (joblist != null && joblist.length() > 0) {
                            jobstatuslist!!.clear()

                            jobstatuslist!!.addAll(
                                Gson().fromJson(
                                    joblist.toString(),
                                    Array<JobStatusModel>::class.java
                                ).toList()
                            )
                            jobOrderAdapter!!.addall(jobstatuslist)


                            if (currentpage.toString() < jsonObject.optString("total_pages")) jobOrderAdapter!!.addloadingfooter()
                            else islastpage = true

                        }
                    } else {
                        Utility().showOkDialog(
                            context!!,
                            resources.getString(R.string.app_name),
                            jsonObject.optString("message")
                        )
                    }


                } else if (statuscode == 403) {
                    Utility().showInactiveDialog(
                        context!!,
                        resources.getString(R.string.app_name),
                        jsonObject.optString("message")
                    )


                } else {
                    Utility().showOkDialog(
                        context!!,
                        resources.getString(R.string.app_name),
                        jsonObject.optString("message")
                    )

                }
                jobOrderAdapter!!.notifyDataSetChanged()


            }


            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                customProgress.hideProgress()
                Utility().showOkDialog(
                    context!!,
                    resources.getString(R.string.app_name),
                    getString(R.string.error_something_is_wrong_ln)
                )
            }

        })
    }

    private fun loadNextPage(
        pagenumber: Int,
        jobstatus: Int,
        filterpriority: Int,
        engineerid: String
    ) {

        val call: Call<JsonObject> =
            apiInterface.jobstatus(pagenumber, jobstatus, filterpriority, engineerid)
        call.enqueue(object : Callback<JsonObject> {


            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                jobOrderAdapter!!.removeloadingfooter()
                isloading = false


                if (response.code() == 200) {

                    val jsonObject = JSONObject(response.body().toString())
                    if (jsonObject.optBoolean("status")) {


                        val data = jsonObject.optJSONObject("data")
                        val joblist = data.optJSONArray("job_list")
                        if (joblist != null && joblist.length() > 0) {
                            jobstatuslist!!.clear()

                            jobstatuslist!!.addAll(
                                Gson().fromJson(
                                    joblist.toString(),
                                    Array<JobStatusModel>::class.java
                                ).toList()
                            )
                            jobOrderAdapter!!.addall(jobstatuslist)

                            if (currentpage.toString() != jsonObject.optString("total_pages")) jobOrderAdapter!!.addloadingfooter()
                            else islastpage = true

                        }
                    } else {
                        Utility().showOkDialog(
                            context!!,
                            resources.getString(R.string.app_name),
                            jsonObject.optString("message")
                        )
                    }
                }
            }


            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Utility().showOkDialog(
                    context!!,
                    resources.getString(R.string.app_name),
                    getString(R.string.error_something_is_wrong_ln)
                )
            }

        })

    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(mReceiver)
    }

    inner class WrapContentLinearLayoutManager(context: Context) : LinearLayoutManager(context) {
        //... constructor
        override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
            try {
                super.onLayoutChildren(recycler, state)
            } catch (e: IndexOutOfBoundsException) {
                Log.e("TAG", "meet a IOOBE in RecyclerView")
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            loadFirstpage()
        }
    }


}
