package com.management.org.dcms.codes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.management.org.dcms.R
import com.management.org.dcms.codes.models.CallSentReportResponse

class CallTestAdapter : RecyclerView.Adapter<CallTestViewHolder>() {
    var list = ArrayList<CallSentReportResponse.CallLogReportModel>()
    fun submitCallLogs(data: ArrayList<CallSentReportResponse.CallLogReportModel>) {
        list.clear()
        list.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallTestViewHolder {
        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.call_test_item, parent, false)
        return CallTestViewHolder(view)
    }

    override fun onBindViewHolder(holder: CallTestViewHolder, position: Int) {
        var data = list[position]
        holder.submitResponse(data)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}