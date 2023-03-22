package com.webclues.IPPSOperator.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.webclues.IPPSOperator.Modelclass.newFaqResponseItem

import com.webclues.IPPSOperator.R
import com.webclues.IPPSOperator.adapter.newFaqAdapter

/**
 * A simple [Fragment] subclass.
 */
class newFaqFragment : Fragment() {

    lateinit var rvfaq: RecyclerView
    lateinit var newFaqAdapter: newFaqAdapter
    var faqarraylist: ArrayList<newFaqResponseItem>? = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_new_faq, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initview(view)
    }

    private fun initview(view: View) {
        rvfaq = view.findViewById(R.id.rvfaq)
        rvfaq.layoutManager = LinearLayoutManager(activity)
        rvfaq.setHasFixedSize(true)
        faqarraylist!!.add(
            newFaqResponseItem(
                resources.getString(R.string.faq_title1),
                resources.getString(R.string.faq_desc)
            )

        )
        faqarraylist!!.add(
            newFaqResponseItem(
                resources.getString(R.string.faq_title2),
                resources.getString(R.string.faq_desc)
            )

        )
        faqarraylist!!.add(
            newFaqResponseItem(
                resources.getString(R.string.faq_title3),
                resources.getString(R.string.faq_desc)
            )

        )
        faqarraylist!!.add(
            newFaqResponseItem(
                resources.getString(R.string.faq_title4),
                resources.getString(R.string.faq_desc)
            )

        )
        faqarraylist!!.add(
            newFaqResponseItem(
                resources.getString(R.string.faq_title5),
                resources.getString(R.string.faq_desc)
            )

        )
        newFaqAdapter= newFaqAdapter(activity!!,faqarraylist!!)
        rvfaq.adapter=newFaqAdapter
    }
}
