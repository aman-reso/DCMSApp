package com.management.org.dcms.codes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.management.org.dcms.R
import com.management.org.dcms.codes.models.TaskItem
import com.management.org.dcms.codes.viewholder.QReportViewHolder

class QReportAdapter : RecyclerView.Adapter<QReportViewHolder>() {
    var reportList = arrayListOf<TaskItem>()
    fun submitList(taskReportList: ArrayList<TaskItem>) {
        reportList.clear()
        reportList.addAll(taskReportList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QReportViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.qreport_item_holder, parent, false)
        return QReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: QReportViewHolder, position: Int) {
        val taskItem = reportList[position]
        holder.bindData(taskItem = taskItem)
    }

    override fun getItemCount(): Int {
        return reportList.size
    }
}