package com.management.org.dcms.codes.viewholder

import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.management.org.dcms.R
import com.management.org.dcms.codes.models.QContactsModel

class QContactsListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var contactListItemCheckBox: CheckBox? = itemView.findViewById(R.id.contactsEachItemCheckBox)
    private var contactListItemMobNumTV: TextView? = itemView.findViewById(R.id.contactsItemMobileNum)
    var sendToWapIcon: View? = itemView.findViewById(R.id.sendToWapIcon)
    fun bindDataWithHolder(item: QContactsModel) {
        contactListItemMobNumTV?.text = item.MobileNo
        if(item.SentStatus==0){
            contactListItemCheckBox?.isChecked=false
        }
        else if (item.SentStatus==1){
            contactListItemCheckBox?.isChecked=true
        }
    }

}