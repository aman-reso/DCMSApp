package com.management.org.dcms.codes.viewholder

import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.management.org.dcms.R
import com.management.org.dcms.codes.models.ContactsModel
import org.w3c.dom.Text

class ContactsListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var contactListItemCheckBox: CheckBox? = itemView.findViewById(R.id.contactsEachItemCheckBox)
    private var contactListItemMobNumTV: TextView? = itemView.findViewById(R.id.contactsItemMobileNum)
    var sendToWapIcon: ImageView? = itemView.findViewById(R.id.sendToWapIcon)
    fun bindDataWithHolder(item: ContactsModel) {
        contactListItemMobNumTV?.text = item.WANo

    }

}