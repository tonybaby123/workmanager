package net.appitiza.moderno.ui.model

import java.io.Serializable

class CurrentCheckIndata : Serializable {
    var documentid: String? = null
    var siteid: String? = ""
    var sitename: String? = null
    var checkintime: String? = ""
    var checkouttime: String? = ""
    var useremail: String? = null
    var payment: String? = null


}