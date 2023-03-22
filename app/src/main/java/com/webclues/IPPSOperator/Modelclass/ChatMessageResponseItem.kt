package com.webclues.IPPSOperator.Modelclass

class ChatMessageResponseItem {
    var type: Int = 0
    var Message: String? = null

    constructor(type: Int, Message: String) {
        this.type = type
        this.Message = Message
    }
}