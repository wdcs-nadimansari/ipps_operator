package com.webclues.IPPSOperator.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.webclues.IPPSOperator.Modelclass.newFaqResponseItem
import com.webclues.IPPSOperator.R
import kotlinx.android.synthetic.main.adp_faq.view.*


class newFaqAdapter(
    private val context: Context,
    private val faqarraylist: ArrayList<newFaqResponseItem>
) : RecyclerView.Adapter<newFaqAdapter.Myviewholder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Myviewholder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.adp_faq, parent, false)
        return Myviewholder(view)
    }

    override fun getItemCount(): Int {
        return faqarraylist.size
    }

    override fun onBindViewHolder(holder: Myviewholder, position: Int) {
        val faqResponseItem = faqarraylist.get(position)
        holder.txtGropItem.text = faqResponseItem.title
        holder.txtchildItem.text = faqResponseItem.description
        holder.itemView.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {

            }

        })


    }

    class Myviewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        var txtGropItem = itemView.txtGroupItem
        var ivindiactor = itemView.ivIndicator
        var txtchildItem = itemView.txtChildItem
    }
}