package com.management.org.dcms.codes.models

import com.google.gson.annotations.SerializedName

data class ProfileResponseModel(
    var Message: String?,
    var Status: Int,
    @SerializedName("Profile")
    var profile: Profile
)

data class Profile(
    var Id: Int, var Name: String,
    var LoginId: String?,
    var MobileNo: String?, var EmailId: String?, var StateName: String?,
    var DistrictName: String?, var BlockName: String?, var VillageName: String?,var GramPanchayatName:String?
)


//{
//  "Message": "string",
//  "Status": 0,
//  "Profile": {
//    "Id": 0,
//    "Name": "string",
//    "LoginId": "string",
//    "MobileNo": "string",
//    "EmailId": "string",
//    "StateName": "string",
//    "DistrictName": "string",
//    "BlockName": "string",
//    "GramPanchayatName": "string",
//    "VillageName": "string"
//  }
//}
