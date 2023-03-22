package com.webclues.IPPSOperator.Interface

import com.webclues.IPPSOperator.Modelclass.GroupListModel


interface OnChatListUpdate {
    fun UpdateList(chatList : ArrayList<GroupListModel>)
}