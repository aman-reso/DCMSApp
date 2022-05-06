package com.management.org.dcms.codes.models

import com.google.gson.annotations.SerializedName

data class ContactsMainModel(
    var Message: String,
    @SerializedName("SGAccount") var contactList: ArrayList<ContactsModel>?,
    @SerializedName("QSGAccount") var qContactsList: ArrayList<ContactsModel>?

)

data class ContactsModel(
    var Id: Int, var MobileNo: String, var WANo: String,
    var HHId: Int,
    var SentTime: String,
    var SentStatus: Int,
    var HHName:String=""
)

data class QContactsMainModel(
    var Message: String,
    @SerializedName("SGQAccount")
    var qContactsList: ArrayList<QContactsModel>?,
    @SerializedName("SGAccount")
    var contactsList: ArrayList<QContactsModel>?
)

data class QContactsModel(
    var Id: Int,
    var MobileNo: String,
    var WANo: String,
    var HHId: Int,
    var SentTime: String,
    var SentStatus: Int,
    var QStatus: Int,
    var HHName: String = ""
)


//{
//  "Message": "string",
//  "status": 0,
//  "SGAccount": [
//    {
//      "Id": 0,
//      "MobileNo": "string",
//      "WANo": "string"
//    }
//  ]
//}