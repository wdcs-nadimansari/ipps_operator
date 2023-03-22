package com.webclues.IPPSOperator.fragments


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.webclues.IPPSOperator.Modelclass.GroupListModel
import com.webclues.IPPSOperator.adapter.ChatAdapter
import com.webclues.IPPSOperator.Interface.OnChatListUpdate
import com.webclues.IPPSOperator.R
import com.webclues.IPPSOperator.activity.MainActivity
import com.webclues.IPPSOperator.application.mApplicationClass
import com.webclues.IPPSOperator.utility.Log

import kotlinx.android.synthetic.main.fragment_chat.*

/**
 * A simple [Fragment] subclass.
 */
class ChatFragment : Fragment(), OnChatListUpdate {

    lateinit var chatadapter: ChatAdapter
    private var groupList: ArrayList<GroupListModel> = arrayListOf()
    private var groupListTemp: ArrayList<GroupListModel> = arrayListOf()
    lateinit var mContext: Context
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_chat, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = context!!
        mApplicationClass.onChatlistUpdate = this
        SetChatData()
    }


    private fun SetChatData() {
        groupList.addAll(mApplicationClass.getInstance()?.getChatList()!!)
        //Collections.sort(messageList, Comparator<ChatListModel> { t1, s -> s.lastUpdate.compareTo(t1.lastUpdate) })
        Log.e("Grouplist>>",groupList.size)
        groupListTemp.clear()
        if(!groupList.isNullOrEmpty()){
           /* for (i in 0 until groupList!!.size) {
                if (groupList!![i].has_message) {
                    groupListTemp?.add(groupList!![i])
                }
            }
            //Collections.sort(messageListTemp, Comparator<ChatListModel> { t1, s -> s.lastUpdate.compareTo(t1.lastUpdate) })
            groupListTemp?.sortedWith(compareBy(GroupListModel::lastUpdate, GroupListModel::lastUpdate))*/
            groupListTemp?.addAll(groupList!!)
            groupListTemp?.sortedWith(compareBy(GroupListModel::lastUpdate, GroupListModel::lastUpdate))
            SetAdapter(activity!!, groupListTemp!!)
        }


    }

    private fun SetAdapter(context: Context, chatarraylist: ArrayList<GroupListModel>) {
        rvChat.layoutManager = LinearLayoutManager(context)
        rvChat.setHasFixedSize(true)

        chatadapter = ChatAdapter(context, chatarraylist)
        rvChat.adapter = chatadapter
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).txtTitle.setText(resources.getString(R.string.chat))
    }

    override fun UpdateList(chatList: ArrayList<GroupListModel>) {
        Log.e("OnChatlist Update-->", Gson().toJson(chatList))
        groupList.clear()
        groupList.addAll(chatList)
        groupListTemp.clear()

        for (i in groupList.indices) {
            if (groupList[i].has_message) {
                groupListTemp.add(groupList[i])
            }
        }
        Log.e("messageListTemp", "" + Gson().toJson(groupListTemp))
        if (groupListTemp.size > 0) {

            groupListTemp.sortedWith(compareBy(GroupListModel::lastUpdate, GroupListModel::lastUpdate))

           /* txtNoRecord.visibility = View.GONE
            rvChat.visibility = View.VISIBLE*/
        } else {
          /*  txtNoRecord.visibility = View.VISIBLE
            rvChat.visibility = View.GONE*/
        }
        chatadapter.notifyDataSetChanged()
    }

}
