package com.webclues.IPPSOperator.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.webclues.IPPSOperator.Modelclass.NotificationModel
import com.webclues.IPPSOperator.R
import com.webclues.IPPSOperator.utility.Content

class NotificationAdapter(
    private val context: Activity

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var ITEM: Int = 0
    private var LOADING = 1
    var isloadingadded = false
    val notificationlist: ArrayList<NotificationModel>? = arrayListOf()


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {

        var viewholder: RecyclerView.ViewHolder? = null

        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            ITEM -> {
                val viewItem = inflater.inflate(R.layout.adp_notifications, parent, false)
                viewholder = Myviewholder(viewItem)

            }

            LOADING -> {
                val viewLoading = inflater.inflate(R.layout.pagination_progressbar, parent, false)
                viewholder = LoadingViewholder(viewLoading)
            }

        }

        return viewholder!!
    }


    override fun getItemCount(): Int {
        return if (notificationlist == null) 0 else notificationlist.size

    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var notificationModel: NotificationModel = notificationlist!!.get(position)


        when (getItemViewType(position)) {
            ITEM -> {
                val holder = holder as Myviewholder
                holder.txtTime.text =
                    Content.millisToLongDHMS(notificationModel.date_time!!)
                holder.txtDescription.text = notificationModel.content

            }
            LOADING -> {
                showloadingview(holder as LoadingViewholder, position)

            }
        }

    }

    fun add(notificationModel: NotificationModel?) {
        notificationlist!!.add(notificationModel!!)
        notifyItemInserted(notificationlist.size - 1)


    }


    fun addall(list: ArrayList<NotificationModel>?) {
        for (notificationmodel in list!!) {
            add(notificationmodel)
        }

    }

    fun remove(notificationModel: NotificationModel) {
        var position: Int = notificationlist!!.indexOf(notificationModel)
        if (position > -1) {
            notificationlist.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear() {
        isloadingadded = false
        while (itemCount > 0) {
            remove(getItem(0))
        }
    }



    fun addloadingfooter() {
        isloadingadded = true
        add(NotificationModel())

    }

    fun removeloadingfooter() {
        isloadingadded = false

        val position = notificationlist!!.size - 1
        val result = getItem(position)

        if (result != null) {
            notificationlist.removeAt(position)
            notifyItemRemoved(position)
        }

    }

    override fun getItemViewType(position: Int): Int {


        if (position == notificationlist!!.size - 1 && isloadingadded) {
            return LOADING

        } else {
            return ITEM
        }


    }


    fun getItem(position: Int): NotificationModel {
        return notificationlist!!.get(position)
    }

    fun showloadingview(viewholder: LoadingViewholder, position: Int) {

    }


    class Myviewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var txtTime = itemView.findViewById(R.id.txtTime) as TextView
        var txtDescription = itemView.findViewById(R.id.txtDescription) as TextView


    }

    class LoadingViewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var progressBar = itemView.findViewById(R.id.progressBar) as ProgressBar
    }

}