package com.webclues.IPPSOperator.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.webclues.IPPSOperator.R
import com.webclues.IPPSOperator.activity.MainActivity
import com.webclues.IPPSOperator.adapter.FaqAdapter
import java.util.*
import kotlin.collections.ArrayList
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.webclues.IPPSOperator.service.APIClient
import com.webclues.IPPSOperator.Modelclass.FaqResponse
import com.webclues.IPPSOperator.service.checkNetworkState
import com.webclues.IPPSOperator.utility.CustomProgress
import com.webclues.IPPSOperator.utility.Utility
import com.webclues.IPPSOperator.service.ApiInterface
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.HashMap


/**
 * A simple [Fragment] subclass.
 */
class FaqFragment : Fragment(), ExpandableListView.OnChildClickListener {


    lateinit var elvFaq: ExpandableListView
    lateinit var faqAdapter: FaqAdapter
    var width: Int = 0
    lateinit var customProgress: CustomProgress
    lateinit var apiInterface: ApiInterface
    var faqlist: ArrayList<FaqResponse> = arrayListOf()
    val prevExpandPosition = intArrayOf(-1)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_faq, container, false)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initview(view)
    }

    private fun initview(view: View) {
        customProgress = CustomProgress.instance
        elvFaq = view.findViewById(R.id.elvFaq)
        elvFaq.setOnChildClickListener(this)

        if (checkNetworkState(context!!)) {
            faq()
        } else {
            Utility().showOkDialog(
                context!!,
                resources.getString(R.string.app_name),
                resources.getString(R.string.error_internet_ln)
            )
        }

    }

    override fun onChildClick(
        parent: ExpandableListView?,
        v: View?,
        groupPosition: Int,
        childPosition: Int,
        id: Long
    ): Boolean {
        val index = elvFaq.getFlatListPosition(
            ExpandableListView.getPackedPositionForChild(
                groupPosition,
                childPosition
            )
        )
        elvFaq.setItemChecked(index, true)

        return true
    }


    private fun faq() {
        customProgress.showProgress(context!!, getString(R.string.please_wait), false)
        apiInterface = APIClient.getretrofit(context!!).create(ApiInterface::class.java)
        val call: Call<JsonObject> = apiInterface.faq()
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                customProgress.hideProgress()
                val jsonObject = JSONObject(response.body().toString())
                val statuscode = jsonObject.optInt("status_code")
                if (statuscode == 200) {
                    if (jsonObject.optBoolean("status")) {
                        val data = jsonObject.optJSONObject("data")
                        val faqdata = data.getJSONArray("faqs_list")
                        if (faqdata != null && faqdata.length() > 0) {
                            faqlist.clear()
                            faqlist.addAll(
                                Gson().fromJson(
                                    faqdata.toString(),
                                    Array<FaqResponse>::class.java
                                ).toList()
                            )
                            SetFaqlist(faqlist)
                        }

                    } else {
                        Utility().showOkDialog(
                            activity!!,
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
                customProgress.hideProgress()
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
        (activity as MainActivity).txtTitle.text = resources.getString(R.string.faq)
    }

    private fun SetFaqlist(faqlist: ArrayList<FaqResponse>) {


        val listdatachild: HashMap<String, List<String>> = hashMapOf()
        val listdataheader: ArrayList<String> = arrayListOf()
        for (i in 0..faqlist.size - 1) {
            listdataheader.add(faqlist.get(i).title)

            val listsubmenu: ArrayList<String> = arrayListOf()
            listsubmenu.add(faqlist.get(i).description)
            listdatachild.put(faqlist.get(i).title, listsubmenu)

        }


        faqAdapter =
            FaqAdapter(activity!!, listdataheader, listdatachild)
        elvFaq.setAdapter(faqAdapter)
        elvFaq.setOnGroupExpandListener({ groupPosition ->
            if (prevExpandPosition[0] >= 0 && prevExpandPosition[0] !== groupPosition) {
                elvFaq.collapseGroup(prevExpandPosition[0])

            }

            prevExpandPosition[0] = groupPosition
        })
        elvFaq.setOnGroupCollapseListener {
            object : ExpandableListView.OnGroupCollapseListener {
                override fun onGroupCollapse(groupPosition: Int) {
                    elvFaq.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.white))

                }

            }
        }

    }

}
