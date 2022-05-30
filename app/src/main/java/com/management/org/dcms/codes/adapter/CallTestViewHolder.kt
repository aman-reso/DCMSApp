package com.management.org.dcms.codes.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.management.org.dcms.R
import com.management.org.dcms.codes.models.CallSentReportResponse

class CallTestViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {

    private var tv:TextView=itemView.findViewById(R.id.mobileNoCR)

    fun submitResponse(data: CallSentReportResponse.CallLogReportModel) {
       tv.text=data.mobileNo
    }
}