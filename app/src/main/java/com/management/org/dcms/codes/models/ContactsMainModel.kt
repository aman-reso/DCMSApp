package com.management.org.dcms.codes.models

import com.google.gson.annotations.SerializedName

data class ContactsMainModel(
    var Message: String,
    @SerializedName("SGAccount") var contactList: ArrayList<ContactsModel>
)

data class ContactsModel(var Id: Int, var MobileNo: String, var WANo: String)
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