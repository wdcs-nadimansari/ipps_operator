package com.webclues.IPPSOperator.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.webclues.IPPSOperator.service.APIClient
import com.webclues.IPPSOperator.Modelclass.NotificationModel
import com.webclues.IPPSOperator.R
import com.webclues.IPPSOperator.activity.MainActivity
import com.webclues.IPPSOperator.adapter.NotificationAdapter
import com.webclues.IPPSOperator.adapter.PaginationScrollListener
import com.webclues.IPPSOperator.service.checkNetworkState
import com.webclues.IPPSOperator.utility.CustomProgress
import com.webclues.IPPSOperator.utility.Utility
import com.webclues.IPPSOperator.service.ApiInterface
import kotlinx.android.synthetic.main.fragment_notification.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 */
class NotificationFragment : Fragment() {

    lateinit var customProgress: CustomProgress
    lateinit var apiInterface: ApiInterface
    lateinit var notificationAdapter: NotificationAdapter
    var PAGE_START: Int = 1
    var currentpage: Int = PAGE_START
    var isloading: Boolean = false
    var islastpage: Boolean = false
    lateinit var rvNotifications: RecyclerView
    var notificationlist: ArrayList<NotificationModel> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_notification, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
    }

    fun initView(view: View) {
        apiInterface = APIClient.getretrofit(activity!!).create(ApiInterface::class.java)
        customProgress = CustomProgress.instance

        rvNotifications = view.findViewById(R.id.rvNotifications)
        rvNotifications.layoutManager = LinearLayoutManager(
            context!!
        )
        notificationAdapter = NotificationAdapter(activity!!)
        rvNotifications.adapter = notificationAdapter
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

        if (checkNetworkState(context!!)) {
            loadFirstpage()
        } else {
            Utility().showOkDialog(
                context!!,
                resources.getString(R.string.app_name),
                resources.getString(R.string.error_internet_ln)
            )
        }

    }


    fun loadFirstpage(

    ) {
        currentpage = PAGE_START
        customProgress.showProgress(context!!, getString(R.string.please_wait), false)
        val call: Call<JsonObject> =
            apiInterface.notificationslist(currentpage)
        call.enqueue(object : Callback<JsonObject> {


            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                customProgress.hideProgress()
                val jsonObject = JSONObject(response.body().toString())
                val statuscode = jsonObject.optInt("status_code")
                if (statuscode == 200) {
                    if (jsonObject.optBoolean("status")) {
                        val data = jsonObject.optJSONObject("data")
                        val list = data.optJSONArray("notification_list")
                        if (list != null && list.length() > 0) {
                            notificationlist.clear()

                            notificationlist.addAll(
                                Gson().fromJson(
                                    list.toString(),
                                    Array<NotificationModel>::class.java
                                ).toList()
                            )

                            notificationAdapter.addall(notificationlist)


                            if (currentpage.toString() < jsonObject.optString("total_pages")) notificationAdapter.addloadingfooter()
                            else islastpage = true

//                            notificationAdapter.notifyDataSetChanged()
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

    private fun loadNextPage(
        pagenumber: Int

    ) {

        val call: Call<JsonObject> =
            apiInterface.notificationslist(pagenumber)
        call.enqueue(object : Callback<JsonObject> {


            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                notificationAdapter.removeloadingfooter()
                isloading = false


                if (response.code() == 200) {

                    val jsonObject = JSONObject(response.body().toString())
                    if (jsonObject.optBoolean("status")) {


                        val data = jsonObject.optJSONObject("data")
                        val list = data.optJSONArray("notification_list")
                        if (list != null && list.length() > 0) {
                            notificationlist.clear()

                            notificationlist.addAll(
                                Gson().fromJson(
                                    list.toString(),
                                    Array<NotificationModel>::class.java
                                ).toList()
                            )
                            notificationAdapter.addall(notificationlist)

                            if (currentpage.toString() != jsonObject.optString("total_pages")) notificationAdapter.addloadingfooter()
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

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).txtTitle.text = resources.getString(R.string.notifications)
    }
}
