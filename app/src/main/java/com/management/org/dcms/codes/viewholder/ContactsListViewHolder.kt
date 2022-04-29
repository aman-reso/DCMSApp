package com.management.org.dcms.codes.viewholder

import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.management.org.dcms.R
import com.management.org.dcms.codes.models.ContactsModel

class ContactsListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var contactListItemCheckBox: CheckBox? = itemView.findViewById(R.id.contactsEachItemCheckBox)
    private var contactListItemMobNumTV: TextView? = itemView.findViewById(R.id.contactsItemMobileNum)
    var sendToWapIcon: ImageView? = itemView.findViewById(R.id.modify)
    fun bindDataWithHolder(item: ContactsModel) {
        contactListItemMobNumTV?.text = item.WANo
        if(item.SentStatus==0){
            contactListItemCheckBox?.isChecked=false
        }
        else if (item.SentStatus==1){
            contactListItemCheckBox?.isChecked=true
        }

    }

}