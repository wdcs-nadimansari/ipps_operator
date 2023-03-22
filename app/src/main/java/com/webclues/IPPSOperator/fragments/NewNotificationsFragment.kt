package com.webclues.IPPSOperator.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.webclues.IPPSOperator.service.APIClient
import com.webclues.IPPSOperator.Modelclass.NotificationListModel
import com.webclues.IPPSOperator.R
import com.webclues.IPPSOperator.activity.MainActivity
import com.webclues.IPPSOperator.adapter.NewNotificationsAdapter
import com.webclues.IPPSOperator.adapter.PaginationScrollListener
import com.webclues.IPPSOperator.utility.Content
import com.webclues.IPPSOperator.utility.CustomProgress
import com.webclues.IPPSOperator.utility.Utility
import com.webclues.IPPSOperator.service.ApiInterface
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NewNotificationsFragment : Fragment() {

    lateinit var srlNotifications: SwipeRefreshLayout
    lateinit var rvNotifications: RecyclerView
    lateinit var newNotificationsAdapter: NewNotificationsAdapter
    var notificationarraylist: ArrayList<NotificationListModel> = arrayListOf()
    lateinit var customProgress: CustomProgress
    lateinit var apiInterface: ApiInterface
    var PAGE_START: Int = 1
    var currentpage: Int = PAGE_START
    var isloading: Boolean = false
    var islastpage: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_new_notifications, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initview(view)
    }

    private fun initview(view: View) {
        customProgress = CustomProgress.instance
        apiInterface = APIClient.getretrofit(context!!).create(ApiInterface::class.java)

        isloading = false
        islastpage = false

        Content.JOBTYPE = Content.NOTIFICATION

        srlNotifications = view.findViewById(R.id.srlNotification)
        rvNotifications = view.findViewById(R.id.rvNotifications)

        rvNotifications.layoutManager =
            LinearLayoutManager(context!!)

        newNotificationsAdapter = NewNotificationsAdapter(activity!!)
        rvNotifications.adapter = newNotificationsAdapter
        rvNotifications.addOnScrollListener(object :
            PaginationScrollListener(rvNotifications.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                isloading = true
                currentpage += 1
                loadNextPage(currentpage)
            }

            override fun isLoading(): Boolean {

                return isloading
            }

            override fun isLastPage(): Boolean {

                return islastpage
            }


        })



        srlNotifications.setOnRefreshListener {
            islastpage = false
            newNotificationsAdapter.notificationarraylist!!.clear()
            loadfirstpage()
        }

        loadfirstpage()
    }


    private fun loadfirstpage() {
        srlNotifications.isRefreshing = false
        customProgress.showProgress(context!!, getString(R.string.please_wait), false)
        currentpage = PAGE_START
        val call: Call<JsonObject> = apiInterface.notificationslist(currentpage)
        call.enqueue(object : Callback<JsonObject> {

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                customProgress.hideProgress()
                val jsonObject = JSONObject(response.body().toString())
                val statuscode = jsonObject.optInt("status_code")
                if (statuscode == 200) {
                    if (jsonObject.optBoolean("status")) {
                        val data = jsonObject.optJSONObject("data")
                        val list = data.optJSONArray("notification_list")
                        if (data.optInt("unread_notification_count") != 0) {
                            (activity as MainActivity).BadgeNotification!!.visibility = View.VISIBLE
                        }else{
                            (activity as MainActivity).BadgeNotification!!.visibility = View.GONE

                        }
                        if (data.optInt("unread_job_request_count") != 0) {
                            (activity as MainActivity).BadgeJobStatus!!.visibility = View.VISIBLE
                        }else{
                            (activity as MainActivity).BadgeJobStatus!!.visibility = View.GONE

                        }

                        if (list != null && list.length() > 0) {
                            notificationarraylist.clear()
                            notificationarraylist.addAll(Gson().fromJson(list.toString(), Array<NotificationListModel>::class.java).toList())
                            newNotificationsAdapter.addall(notificationarraylist)


                            if (currentpage.toString() < jsonObject.optString("total_pages")) newNotificationsAdapter.addloadingfooter()
                            else islastpage = true


                        }
                    } else {
                        Utility().showOkDialog(context!!, resources.getString(R.string.app_name), jsonObject.optString("message"))
                    }
                } else if (statuscode == 403) {
                    Utility().showInactiveDialog(context!!, resources.getString(R.string.app_name), jsonObject.optString("message"))
                } else {
                    Utility().showOkDialog(context!!, resources.getString(R.string.app_name), jsonObject.optString("message"))
                }
                newNotificationsAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                customProgress.hideProgress()
                Utility().showOkDialog(context!!, getString(R.string.app_name), getString(R.string.error_something_is_wrong_ln))

            }

        })
    }

    private fun loadNextPage(currentpage: Int) {
        val call: Call<JsonObject> = apiInterface.notificationslist(currentpage)
        call.enqueue(object : Callback<JsonObject> {

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                newNotificationsAdapter.removeloadingfooter()
                isloading = false


                if (response.code() == 200) {
                    val jsonObject = JSONObject(response.body().toString())
                    val statuscode = jsonObject.optInt("status_code")
                    if (jsonObject.optBoolean("status")) {
                        val data = jsonObject.optJSONObject("data")
                        val list = data.optJSONArray("notification_list")
                        if (list != null && list.length() > 0) {
                            notificationarraylist.clear()

                            notificationarraylist.addAll(Gson().fromJson(list.toString(), Array<NotificationListModel>::class.java).toList())

                            newNotificationsAdapter.addall(notificationarraylist)

                            if (currentpage.toString() != jsonObject.optString("total_pages")) newNotificationsAdapter.addloadingfooter()
                            else islastpage = true


                        }
                    } else {
                        Utility().showOkDialog(context!!, resources.getString(R.string.app_name), jsonObject.optString("message"))
                    }
                }

            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Utility().showOkDialog(context!!, getString(R.string.app_name), getString(R.string.error_something_is_wrong_ln))

            }

        })
    }

}
