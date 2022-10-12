package com.management.org.dcms.codes.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.management.org.dcms.R
import com.management.org.dcms.codes.models.CallSentReportResponse
import org.w3c.dom.Text

class CallTestViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {

    private var mobileNumTV:TextView=itemView.findViewById(R.id.mobileNoCR)
    private var themeNameTV:TextView=itemView.findViewById(R.id.themeName)
    private var callDurationTV:TextView=itemView.findViewById(R.id.callTime)
    private var campaignName:TextView=itemView.findViewById(R.id.campaignName)

    internal fun submitResponse(data: CallSentReportResponse.CallLogReportModel) {
        mobileNumTV.text=data.mobileNo
        callDurationTV.text=data.callTime
        themeNameTV.text=data.themeName
        campaignName.text=data.campaignName
    }
}