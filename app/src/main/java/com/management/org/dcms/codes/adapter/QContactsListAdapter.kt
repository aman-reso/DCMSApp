package com.management.org.dcms.codes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.management.org.dcms.R
import com.management.org.dcms.codes.models.ContactsModel
import com.management.org.dcms.codes.models.QContactsModel
import com.management.org.dcms.codes.viewholder.ContactsListViewHolder
import com.management.org.dcms.codes.viewholder.QContactsListViewHolder

class QContactsListAdapter(var callback: (QContactsModel) -> Unit) : RecyclerView.Adapter<QContactsListViewHolder>() {
    private var contactsList = ArrayList<QContactsModel>()

    fun submitData(contactsList: ArrayList<QContactsModel>) {
        this.contactsList.clear()
        this.contactsList.addAll(contactsList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QContactsListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.qcontacts_each_item_holder, parent, false)
        return QContactsListViewHolder(view)
    }

    override fun onBindViewHolder(holder: QContactsListViewHolder, position: Int) {
        val item = contactsList[position]
        holder.bindDataWithHolder(item)
        holder.sendToWapIcon?.setOnClickListener {
            if (item != null) {
                callback.invoke(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return contactsList.size
    }
}