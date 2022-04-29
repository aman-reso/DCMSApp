package com.management.org.dcms.codes.viewholder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.management.org.dcms.R
import com.management.org.dcms.codes.models.TaskItem

class QReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var wANoValueTV: TextView? = itemView.findViewById(R.id.qReportWhatsNumValueTV)
    var startTimeValueTV: TextView? = itemView.findViewById(R.id.attemptTimeValueTV)
    var finishTimeValueTV: TextView? = itemView.findViewById(R.id.finishTimeValueTV)
    var themeNameValueTV: TextView? = itemView.findViewById(R.id.themeNameValueTV)

    fun bindData(taskItem: TaskItem) {
        wANoValueTV?.text = taskItem.WANo
        themeNameValueTV?.text = taskItem.ThemeName
        finishTimeValueTV?.text = taskItem.FinishTime
        startTimeValueTV?.text = taskItem.AttemptTime
        startTimeValueTV?.text=taskItem.SentTime
    }
}