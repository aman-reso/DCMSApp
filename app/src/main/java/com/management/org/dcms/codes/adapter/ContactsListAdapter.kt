package com.management.org.dcms.codes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.management.org.dcms.R
import com.management.org.dcms.codes.models.ContactsModel
import com.management.org.dcms.codes.viewholder.ContactsListViewHolder

//will shift it to Async Diff utils
class ContactsListAdapter(var callback: (ContactsModel) -> Unit) : RecyclerView.Adapter<ContactsListViewHolder>() {
    private var contactsList = ArrayList<ContactsModel>()

    fun submitData(contactsList: ArrayList<ContactsModel>) {
        this.contactsList.clear()
        this.contactsList.addAll(contactsList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_contact_each_item, parent, false)
        return ContactsListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactsListViewHolder, position: Int) {
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