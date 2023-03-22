package com.webclues.IPPSOperator.Modelclass

class NotificationResponseItem {

    var NotificationTime: String? = null
    var NotificationDesc: String? = null

    constructor(NotificationTime: String, NotificationDesc: String) {

        this.NotificationTime = NotificationTime
        this.NotificationDesc = NotificationDesc

    }
}