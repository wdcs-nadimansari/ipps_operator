package com.webclues.IPPSOperator.Modelclass

import com.webclues.IPPSOperator.Modelclass.ChatMemberModel

class GroupListModel {
    var id = ""
//    var user_id: String = ""
    var group_name: String = ""
    var lastMessage: String = ""
    var profile_pic: String = ""
    var has_message : Boolean=false
//    var receiverUid : String=""
    var chatMembers : ArrayList<ChatMemberModel> = ArrayList()
    var lastUpdate = Long.MAX_VALUE

    var unreadCount = 0
}