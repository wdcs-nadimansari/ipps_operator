package com.webclues.IPPSOperator.Modelclass

class MetalResponseItem {

    var metalimage: Int? = null
    var metalpriority: String? = null
    var metalname: String? = null
    var metaldate: String? = null
    var metaldesc: String? = null
    var metalstatus: String? = null
    var problemname:String?=null
    var EngineerName:String?=null

    constructor(
        metalimage: Int, metalpriority: String, metalname: String,
        metaldesc: String,problemname:String, metaldate: String, metalstatus: String,EngineerName:String
    ) {

        this.metalimage = metalimage
        this.metalpriority = metalpriority
        this.metalname = metalname
        this.metaldesc = metaldesc
        this.problemname=problemname
        this.metaldate = metaldate
        this.metalstatus = metalstatus
        this.EngineerName=EngineerName



    }


}