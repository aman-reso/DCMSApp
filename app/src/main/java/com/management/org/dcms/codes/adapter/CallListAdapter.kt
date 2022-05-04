package com.management.org.dcms.codes.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.management.org.dcms.R
import com.management.org.dcms.codes.models.QContactsModel
import com.management.org.dcms.codes.utility.Utility
import com.management.org.dcms.codes.viewholder.QContactsListViewHolder

class CallListAdapter(var callback: (QContactsModel, Int) -> Unit) : RecyclerView.Adapter<QContactsListViewHolder>() {

    val intent = Intent(Intent.ACTION_DIAL)

    private var contactsList = ArrayList<QContactsModel>()

    fun submitData(contactsList: ArrayList<QContactsModel>) {
        this.contactsList.clear()
        this.contactsList.addAll(contactsList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QContactsListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.call_item_holder, parent, false)
        return QContactsListViewHolder(view)
    }

    override fun onBindViewHolder(holder: QContactsListViewHolder, position: Int) {
        val item = contactsList[position]
        holder.bindDataWithHolder(item)
        holder.actionBtn?.setOnClickListener {
            if (item != null && item.QStatus == 0) {
                callback.invoke(item, position)
            } else {
                Utility.showToastMessage("Already completed")
            }
        }
    }

    override fun getItemCount(): Int {
        return contactsList.size
    }
}